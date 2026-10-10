package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.DisposalCreateRequestDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointDisposalsSummaryDTO;
import br.com.maisprati.projeto.dto.response.DisposalResponseDTO;
import br.com.maisprati.projeto.service.DisposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/disposals")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Descarte de tecidos", description = "Endpoints para gerenciamento de descarte de tecidos")
public class DisposalController {

    private final DisposalService disposalService;

    @PostMapping
    // 1. Restrição de nível de API: Apenas estes perfis podem aceder
    @PreAuthorize("hasAnyRole('ADMIN', 'PONTO_COLETA_GERENTE', 'PONTO_COLETA_OPERADOR')")
    @Operation(summary = "Registrar descarte", description = "Apenas administradores, gerentes ou operadores do ponto podem registrar a entrada de descartes.")
    public ResponseEntity<DisposalResponseDTO> registerDisposal(
            @Valid @RequestBody DisposalCreateRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(disposalService.registerDisposal(request, userDetails.getUsername()));
    }

    @GetMapping("/collection-point/{collectionPointId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PONTO_COLETA_GERENTE', 'PONTO_COLETA_OPERADOR')")
    @Operation(summary = "Listar descartes do ponto", description = "Lista os itens descartados e o peso total (Apenas Gerentes, Operadores e Admins).")
    public ResponseEntity<CollectionPointDisposalsSummaryDTO> getDisposalsByCollectionPoint(
            @PathVariable Long collectionPointId,
            @ParameterObject Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {

        CollectionPointDisposalsSummaryDTO response = disposalService.getDisposalsByCollectionPoint(collectionPointId, userDetails.getUsername(), pageable);
        return ResponseEntity.ok(response);
    }
}