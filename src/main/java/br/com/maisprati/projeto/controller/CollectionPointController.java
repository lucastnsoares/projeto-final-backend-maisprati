package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.OperatorCreateRequestDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointResponseDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointUsersResponseDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointDistanceResponseDTO;
import br.com.maisprati.projeto.service.CollectionPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/collection-points")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pontos de Coleta", description = "Endpoints para gerenciamento de pontos de coleta")
public class CollectionPointController {

        private final CollectionPointService collectionPointService;

        @GetMapping("/{id}")
        @PreAuthorize("isAuthenticated()")
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
        @PreAuthorize("isAuthenticated()")
        @Operation(summary = "Buscar pontos de coleta mais próximos por raio de distância")
        public ResponseEntity<Page<CollectionPointDistanceResponseDTO>> findNearby(
                        @Parameter(description = "Latitude do usuário", example = "-19.9167", required = true) @RequestParam BigDecimal lat,
                        @Parameter(description = "Longitude do usuário", example = "-43.9345", required = true) @RequestParam BigDecimal lng,
                        @Parameter(description = "Raio máximo de busca em km (padrão 15km)", example = "10.0") @RequestParam(required = false, defaultValue = "15.0") Double radiusKm,
                        @Parameter(description = "IDs dos tipos de tecidos a serem considerados na busca", example = "[1, 2, 3]") @RequestParam(required = false) List<Long> clothTypeIds,
                        @ParameterObject @PageableDefault(page = 0, size = 10) Pageable pageable) {
                return ResponseEntity.ok(collectionPointService.findNearby(lat, lng, radiusKm, clothTypeIds, pageable));
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