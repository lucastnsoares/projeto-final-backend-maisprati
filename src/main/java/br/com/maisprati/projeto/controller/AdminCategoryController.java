package br.com.maisprati.projeto.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.com.maisprati.projeto.dto.request.CategoryRequestDTO;
import br.com.maisprati.projeto.dto.response.CategoryResponseDTO;
import br.com.maisprati.projeto.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Administração de Categorias", description = "Gerenciamento e controle de categorias pelos administradores do sistema")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Listar todas as categorias", description = "Visualização de todas as categorias de forma paginada")
    public ResponseEntity<Page<CategoryResponseDTO>> findAllCategories(
            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(categoryService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID", description = "Visualização de uma categoria específica por ID")
    public ResponseEntity<CategoryResponseDTO> findCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findCategoryById(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova categoria", description = "Criação de uma nova categoria")
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody @Valid CategoryRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(dto));
    }
}
