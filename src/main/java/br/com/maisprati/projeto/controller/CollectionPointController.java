package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.CollectionPointUpdateDTO;
import br.com.maisprati.projeto.dto.request.CollectionPointCreateRequestDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointResponseDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointDistanceResponseDTO;
import br.com.maisprati.projeto.service.CollectionPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
        public ResponseEntity<CollectionPointResponseDTO> getCollectionPointByIdAndStatusActiveEntity(
                        @PathVariable Long id) {
                CollectionPointResponseDTO response = collectionPointService.getCollectionPointByIdAndStatusActive(id);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Listar pontos aprovados no mapa", description = "SCRUM-179: Retorna apenas pontos de coleta com status APPROVED e que estão ativos.")
        @GetMapping
        public ResponseEntity<Page<CollectionPointResponseDTO>> getPublicApprovedPoints(
                @ParameterObject @PageableDefault(page = 0, size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
                return ResponseEntity.ok(collectionPointService.findPublicApprovedPoints(pageable));
        }

        @GetMapping("/nearby")
        @Operation(summary = "Buscar pontos de coleta mais próximos por raio de distância")
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