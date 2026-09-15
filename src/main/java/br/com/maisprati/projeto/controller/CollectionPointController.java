package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.CollectionPointCreateRequestDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointDistanceResponseDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointResponseDTO;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/collection-points")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pontos de Coleta", description = "Endpoints para gerenciamento de pontos de coleta")
public class CollectionPointController {

        private final CollectionPointService collectionPointService;

        @Operation(summary = "Cadastrar Ponto de Coleta", description = "Permite que um parceiro cadastre um novo ponto de coleta. O ponto será criado com status PENDENTE.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Ponto de coleta cadastrado com sucesso e aguardando aprovação.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CollectionPointResponseDTO.class))),

                        @ApiResponse(responseCode = "400", description = "Erro de validação nos campos informados", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ValidationErrorResponseDTO.class))),

                        @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),

                        @ApiResponse(responseCode = "403", description = "Acesso negado para o perfil atual", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))
        })
        @PostMapping
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<CollectionPointResponseDTO> createCollectionPoint(
                        @Valid @RequestBody CollectionPointCreateRequestDTO requestDTO,
                        @AuthenticationPrincipal UserDetails userDetails,
                        UriComponentsBuilder uriBuilder) {

                CollectionPointResponseDTO response = collectionPointService.createCollectionPoint(requestDTO,
                                userDetails.getUsername());

                URI uri = uriBuilder.path("/collection-points/{id}").buildAndExpand(response.getId()).toUri();

                return ResponseEntity.created(uri).body(response);
        }

        @GetMapping("/{id}")
        @PreAuthorize("isAuthenticated()")
        @Operation(summary = "Buscar ponto de coleta por ID", description = "Permite buscar um ponto de coleta pelo seu ID, exibindo-o de forma detalhada.")
        public ResponseEntity<CollectionPointResponseDTO> getCollectionPointById(@PathVariable Long id) {
                CollectionPointResponseDTO response = collectionPointService.getCollectionPointById(id);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/nearby")
        @PreAuthorize("isAuthenticated()")
        @Operation(summary = "Buscar pontos de coleta mais próximos por raio de distância")
        public ResponseEntity<Page<CollectionPointDistanceResponseDTO>> findNearby(
                        @Parameter(description = "Latitude do usuário", example = "-19.9167", required = true) @RequestParam BigDecimal lat,

                        @Parameter(description = "Longitude do usuário", example = "-43.9345", required = true) @RequestParam BigDecimal lng,

                        @Parameter(description = "Raio máximo de busca em km (padrão 15km)", example = "10.0") @RequestParam(required = false, defaultValue = "15.0") Double radiusKm,

                        @ParameterObject @PageableDefault(page = 0, size = 10) Pageable pageable) {
                return ResponseEntity.ok(collectionPointService.findNearby(lat, lng, radiusKm, pageable));
        }
}