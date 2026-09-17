package br.com.maisprati.projeto.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import br.com.maisprati.projeto.dto.request.CollectionPointUpdateDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointResponseDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointSummaryResponseDTO;
import br.com.maisprati.projeto.service.CollectionPointService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/admin/collection-points")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Administração de Pontos de Coleta", description = "Endpoints para gerenciamento de pontos de coleta pelo administrador")
public class AdminCollectionPointController {

    private final CollectionPointService collectionPointService;

    @GetMapping
    public ResponseEntity<Page<CollectionPointSummaryResponseDTO>> findAll(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(collectionPointService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionPointResponseDTO> getCollectionPointById(@PathVariable Long id) {
        return ResponseEntity.ok().body(collectionPointService.getCollectionPointById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CollectionPointResponseDTO> editCollectionPoint(@PathVariable Long id,
            @RequestBody CollectionPointUpdateDTO dto) {
        return ResponseEntity.ok().body(collectionPointService.updateCollectionPoint(id, dto));
    }
}
