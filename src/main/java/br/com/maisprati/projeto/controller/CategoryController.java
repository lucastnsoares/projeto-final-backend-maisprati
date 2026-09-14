package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.response.CategoryResponseDTO;
import br.com.maisprati.projeto.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Categorias", description = "Visualização de categorias por usuários logados")
public class CategoryController {
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
    @Tag(name = "Categorias", description = "Visualização de uma categoria específica por ID")
    public ResponseEntity<CategoryResponseDTO> findCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findCategoryById(id));
    }
}
