package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.CollectionPointCreateRequestDTO;
import br.com.maisprati.projeto.dto.request.OperatorCreateRequestDTO;
import br.com.maisprati.projeto.dto.response.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import br.com.maisprati.projeto.dto.request.CollectionPointUpdateDTO;
import br.com.maisprati.projeto.service.CollectionPointService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/admin/collection-points")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Administração de Pontos de Coleta", description = "Endpoints para gerenciamento de pontos de coleta pelo administrador")
public class AdminCollectionPointController {

    private final CollectionPointService collectionPointService;

    @Operation(summary = "Cadastrar Ponto de Coleta", description = "Permite que apenas administradores cadastrem novos pontos de coleta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ponto de coleta cadastrado com sucesso e aguardando aprovação.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CollectionPointResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos informados", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ValidationErrorResponseDTO.class))),

            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),

            @ApiResponse(responseCode = "403", description = "Acesso negado para o perfil atual", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<CollectionPointResponseDTO> createCollectionPoint(
            @Valid @RequestBody CollectionPointCreateRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        CollectionPointResponseDTO response = collectionPointService.createCollectionPoint(requestDTO, userDetails.getUsername());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CollectionPointSummaryResponseDTO>> findAll(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(collectionPointService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionPointResponseDTO> getCollectionPointById(@PathVariable Long id) {
        return ResponseEntity.ok().body(collectionPointService.getCollectionPointById(id));
    }

    @GetMapping("/pending-updates")
    @Operation(summary = "Listar edições pendentes", description = "Retorna os pontos de coleta que aguardam aprovação de edição (hasPendingUpdate = true).")
    public ResponseEntity<Page<PendingUpdateResponseDTO>> getPendingUpdates(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(collectionPointService.listPendingUpdates(pageable));
    }

    @PostMapping("/{id}/approve-update")
    @Operation(summary = "Aprovar edição de um ponto", description = "Aplica as alterações propostas por um gerente ao ponto de coleta.")
    public ResponseEntity<CollectionPointResponseDTO> approvePendingUpdate(@PathVariable Long id) {
        return ResponseEntity.ok(collectionPointService.approvePendingUpdate(id));
    }

    @PostMapping("/{id}/reject-update")
    @Operation(summary = "Rejeitar edição de um ponto", description = "Descarta as alterações propostas e remove o status pendente.")
    public ResponseEntity<Void> rejectPendingUpdate(@PathVariable Long id) {
        collectionPointService.rejectPendingUpdate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar pontos pendentes", description = "Retorna uma lista paginada de pontos com status PENDING.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso."),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer perfil ADMIN.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/pending")
    public ResponseEntity<Page<CollectionPointResponseDTO>> getPendingPoints(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(collectionPointService.findPendingCollectionPoints(pageable));
    }

    @Operation(summary = "Aprovar ponto de coleta", description = "Altera o status do ponto para APPROVED e define isActive como true.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ponto de coleta aprovado com sucesso.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CollectionPointResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Ponto de coleta não encontrado.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PatchMapping("/{id}/approve")
    public ResponseEntity<CollectionPointResponseDTO> approveCollectionPoint(@PathVariable Long id) {
        CollectionPointResponseDTO response = collectionPointService.approveCollectionPoint(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Rejeitar ponto de coleta", description = "Altera o status do ponto para REJECTED.")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<CollectionPointResponseDTO> rejectCollectionPoint(@PathVariable Long id) {
        CollectionPointResponseDTO response = collectionPointService.rejectCollectionPoint(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/pause")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Pausar recebimento do ponto de coleta", description = "Permite que um gerente/proprietário do ponto altere o status para PAUSED.")
    public ResponseEntity<CollectionPointResponseDTO> pauseCollectionPoint(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {

        CollectionPointResponseDTO response = collectionPointService.pauseCollectionPoint(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/users")
    @PreAuthorize("hasAnyRole('PONTO_COLETA_GERENTE', 'ADMIN')")
    @Operation(summary = "Listar operadores e gerentes de um ponto de coleta", description = "Permite listar todos os operadores e gerentes associados a um ponto de coleta específico, desde que o usuário autenticado seja um gerente do ponto ou um administrador.")
    public ResponseEntity<CollectionPointUsersResponseDTO> getOperatorsByCollectionPointId(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(collectionPointService.getOperatorsByCollectionPointId(id, userDetails.getUsername()));
    }


    @PatchMapping ("/{id}/users/operators")
    @PreAuthorize("hasAnyRole('PONTO_COLETA_GERENTE', 'ADMIN')")
    @Operation(summary = "Adicionar operador a um ponto de coleta", description = "Permite adicionar um operador a um ponto de coleta específico.")
    public ResponseEntity<CollectionPointUsersResponseDTO> addOperatorToCollectionPoint(
            @PathVariable Long id,
            @RequestBody OperatorCreateRequestDTO operator,
            @AuthenticationPrincipal UserDetails userDetails) {
        CollectionPointUsersResponseDTO response = collectionPointService.addOperatorToCollectionPoint(id, operator, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }



}
