package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.CollectionPointUpdateDTO;
import br.com.maisprati.projeto.dto.request.CollectionPointCreateRequestDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointResponseDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointDistanceResponseDTO;
import br.com.maisprati.projeto.dto.response.ErrorResponseDTO;
import br.com.maisprati.projeto.dto.response.ValidationErrorResponseDTO;
import br.com.maisprati.projeto.service.CollectionPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/collection-points")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pontos de Coleta", description = "Endpoints para gerenciamento de pontos de coleta")
public class CollectionPointController {

        private final CollectionPointService collectionPointService;

        @GetMapping("/{id}")
        @Operation(summary = "Buscar ponto de coleta por ID", description = "Permite buscar um ponto de coleta ativo pelo seu ID, exibindo-o de forma detalhada.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Ponto de coleta encontrado com sucesso.",
                        content = @Content(schema = @Schema(implementation = CollectionPointResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Ponto de coleta não encontrado ou inativo.",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        public ResponseEntity<CollectionPointResponseDTO> getCollectionPointByIdAndStatusActiveEntity(
                        @PathVariable Long id) {
                CollectionPointResponseDTO response = collectionPointService.getCollectionPointByIdAndStatusActive(id);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Listar pontos aprovados no mapa", description = "SCRUM-179: Retorna apenas pontos de coleta com status APPROVED e que estão ativos.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Pontos de coleta aprovados listados com sucesso.",
                        content = @Content(schema = @Schema(implementation = CollectionPointResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos.",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @GetMapping
        public ResponseEntity<Page<CollectionPointResponseDTO>> getPublicApprovedPoints(
                @ParameterObject @PageableDefault(page = 0, size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
                return ResponseEntity.ok(collectionPointService.findPublicApprovedPoints(pageable));
        }

        @GetMapping("/nearby")
        @Operation(summary = "Buscar pontos de coleta mais próximos por raio de distância")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Pontos de coleta próximos encontrados com sucesso.",
                        content = @Content(schema = @Schema(implementation = CollectionPointDistanceResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Parâmetros de localização, raio ou paginação inválidos.",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        public ResponseEntity<Page<CollectionPointDistanceResponseDTO>> findNearby(
                        @Parameter(description = "Latitude do usuário", example = "-19.9167", required = true) @RequestParam BigDecimal lat,
                        @Parameter(description = "Longitude do usuário", example = "-43.9345", required = true) @RequestParam BigDecimal lng,
                        @Parameter(description = "Raio máximo de busca em km (padrão 15km)", example = "10.0") @RequestParam(required = false, defaultValue = "15.0") Double radiusKm,
                        @Parameter(description = "IDs dos tipos de tecidos a serem considerados na busca", example = "[1, 2, 3]") @RequestParam(required = false) List<Long> clothTypeIds,
                        @ParameterObject @PageableDefault(page = 0, size = 10) Pageable pageable) {
                return ResponseEntity.ok(collectionPointService.findNearby(lat, lng, radiusKm, clothTypeIds, pageable));
        }

        @PutMapping("/{id}")
        @PreAuthorize("isAuthenticated()")
        @Operation(summary = "Atualizar Ponto de Coleta", description = "Se o usuário for gerente, cria uma solicitação pendente para aprovação. Se for Admin, aplica direto.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Ponto de coleta atualizado ou solicitação criada com sucesso.",
                        content = @Content(schema = @Schema(implementation = CollectionPointResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Erro de validação nos campos informados.",
                        content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
                @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido.",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "Acesso negado para o usuário atual.",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Ponto de coleta não encontrado.",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        public ResponseEntity<CollectionPointResponseDTO> updateCollectionPoint(
                @PathVariable Long id,
                @Valid @RequestBody CollectionPointUpdateDTO requestDTO,
                @AuthenticationPrincipal UserDetails userDetails) {

                CollectionPointResponseDTO response = collectionPointService.updateCollectionPoint(id, requestDTO, userDetails.getUsername());
                return ResponseEntity.ok(response);
        }

        @PostMapping
        @PreAuthorize("isAuthenticated()")
        @Operation(summary = "Criar Ponto de Coleta", description = "Solicita a criação de um novo ponto de coleta.")
        @ApiResponses({
                @ApiResponse(responseCode = "202", description = "Solicitação de criação recebida com sucesso.",
                        content = @Content(schema = @Schema(implementation = CollectionPointResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Erro de validação nos campos informados.",
                        content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
                @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido.",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "Acesso negado para o usuário atual.",
                        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        public ResponseEntity<CollectionPointResponseDTO> createCollectionPoint(
                @Valid @RequestBody CollectionPointCreateRequestDTO dto,
                @AuthenticationPrincipal UserDetails userDetails) {

                CollectionPointResponseDTO response = collectionPointService.createCollectionPoint(dto, userDetails.getUsername());
                URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.getId())
                        .toUri();
                
                return ResponseEntity.status(HttpStatus.ACCEPTED).location(location).body(response);
        }

}