package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.ClothTypeRequestDTO;
import br.com.maisprati.projeto.dto.request.ClothTypeUpdateRequestDTO;
import br.com.maisprati.projeto.dto.response.ClothTypeResponseDTO;
import br.com.maisprati.projeto.service.ClothTypeService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/cloth-types")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Administração de Tipos de Tecido", description = "Gerenciamento e controle de tipos de tecido pelo Administrador")
public class ClothTypeController {
    private final ClothTypeService clothTypeService;

    @GetMapping
    public ResponseEntity<Page<ClothTypeResponseDTO>> getClothTypes(@ParameterObject @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        var page = clothTypeService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClothTypeResponseDTO> getClothTypeByid(@PathVariable Long id) {
        return ResponseEntity.ok().body(clothTypeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ClothTypeResponseDTO> createClothType(@RequestBody @Valid ClothTypeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clothTypeService.createClothType(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClothTypeResponseDTO> updateClothType(
            @PathVariable Long id,
            @RequestBody @Valid ClothTypeUpdateRequestDTO dto) {
        return ResponseEntity.ok(clothTypeService.updateClothType(id, dto));
    }

}
