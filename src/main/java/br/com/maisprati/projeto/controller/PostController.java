package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.response.PostResponseDTO;
import br.com.maisprati.projeto.dto.response.PostSummaryResponseDTO;
import br.com.maisprati.projeto.service.PostService;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Conteúdo do site", description = "Visualização de posts")
public class PostController {
    private final PostService postService;

    @Operation(summary = "Listar todos os posts publicados de forma resumida e paginada", description = "Retorna os posts publicados de forma resumida. Usuário logados podem acessar este endpoint")
    @GetMapping
    public ResponseEntity<Page<PostSummaryResponseDTO>> findAllByPublishedTrue(
        @Parameter(description = "ID da categoria para filtrar os posts. Se não informado, retorna posts de todas as categorias.")
        @RequestParam(required = false) Long categoryId,

        @ParameterObject
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC)
        Pageable pageable) {
        return ResponseEntity.ok(postService.findAllByPublishedTrue(categoryId,pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retornar um post específico", description = "Retorna um post específico. Usuário logados podem acessar este endpoint")
    public ResponseEntity<PostResponseDTO> findPostByIdAndIsPublishedTrue(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findPostByIdAndIsPublishedTrue(id));
    }

}
