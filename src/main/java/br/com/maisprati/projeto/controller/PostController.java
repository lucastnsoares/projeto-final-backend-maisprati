package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.response.PostResponseDTO;
import br.com.maisprati.projeto.dto.response.PostSummaryResponseDTO;
import br.com.maisprati.projeto.dto.response.ErrorResponseDTO;
import br.com.maisprati.projeto.service.PostService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Conteúdo do site", description = "Visualização de posts")
public class PostController {
    private final PostService postService;

    @Operation(summary = "Listar todos os posts publicados de forma resumida e paginada", description = "Retorna os posts publicados de forma resumida. Usuário logados podem acessar este endpoint")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista paginada de posts publicados retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Parâmetros de consulta inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"O parâmetro informado é inválido.\", \"path\": \"/posts\"}"))),
        @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Token de autenticação ausente ou inválido.\", \"path\": \"/posts\"}")))
    })
    @GetMapping
    public ResponseEntity<Page<PostSummaryResponseDTO>> findAllByPublishedTrue(
        @Parameter(description = "ID da categoria para filtrar os posts. Se não informado, retorna posts de todas as categorias.")
        @RequestParam(required = false) 
        Long categoryId,

        @Parameter(description = "Termo de busca para filtrar os posts.")
        @RequestParam(required = false)
        String searchTerm,

        @Parameter(description = "Lista de tags para filtrar os posts. Se não informado, retorna posts de todas as tags.")
        @RequestParam(required = false)
        Set<String> tags,

        @ParameterObject
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC)
        Pageable pageable) {
        return ResponseEntity.ok(postService.findAllByPublishedTrue(categoryId, searchTerm, tags, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retornar um post específico", description = "Retorna um post específico. Usuário logados podem acessar este endpoint")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Post publicado localizado",
            content = @Content(schema = @Schema(implementation = PostResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Post inexistente ou não disponível",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Post inexistente ou não disponível.\", \"path\": \"/posts/99\"}"))),
        @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Token de autenticação ausente ou inválido.\", \"path\": \"/posts/1\"}")))
    })
    public ResponseEntity<PostResponseDTO> findPostByIdAndIsPublishedTrue(
        @Parameter(description = "ID do post publicado", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(postService.findPostByIdAndIsPublishedTrue(id));
    }

}
