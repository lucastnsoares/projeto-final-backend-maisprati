package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.PostCreateRequestDTO;
import br.com.maisprati.projeto.dto.request.PostDeleteRequestDTO;
import br.com.maisprati.projeto.dto.request.PostEditRequestDTO;
import br.com.maisprati.projeto.dto.response.PostResponseDTO;
import br.com.maisprati.projeto.dto.response.PostSummaryResponseDTO;
import br.com.maisprati.projeto.dto.response.AdminPostResponseDTO;
import br.com.maisprati.projeto.dto.response.AdminPostSummaryResponseDTO;
import br.com.maisprati.projeto.dto.response.ErrorResponseDTO;
import br.com.maisprati.projeto.dto.response.ValidationErrorResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.service.PostService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.Set;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/admin/posts")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Administração de Conteúdo", description = "Gerenciamento e controle de posts para administradores")
@SecurityRequirement(name = "bearerAuth")
public class AdminPostController {

    private final PostService postService;

    @GetMapping
    @Operation(summary = "Listar todos os posts existentes de forma resumida e paginada", description = "Retorna os posts existentes de forma resumida. Apenas usuários com role ADMIN podem acessar este endpoint.")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de posts retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de consulta inválidos",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Parâmetros de consulta inválidos.\", \"path\": \"/admin/posts\"}"))),
            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Token de autenticação ausente ou inválido.\", \"path\": \"/admin/posts\"}"))),
            @ApiResponse(responseCode = "403", description = "Acesso negado: requer perfil ADMIN",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 403, \"error\": \"Forbidden\", \"message\": \"Você não possui autorização para acessar este recurso.\", \"path\": \"/admin/posts\"}")))
        })
    public ResponseEntity<Page<AdminPostSummaryResponseDTO>> findAllPosts(
            @Parameter(description = "ID da categoria para filtrar os posts. Se não informado, retorna posts de todas as categorias.") 
            @RequestParam(required = false) 
            Long categoryId,

            @Parameter(description = "Termo de busca para filtrar os posts.")
            @RequestParam(required = false)
            String searchTerm,

            @Parameter(description = "Tags para filtrar os posts.")
            @RequestParam(required = false)
            Set<String> tags,

            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(postService.findAllPosts(categoryId, searchTerm, tags, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um post específico por ID", description = "Retorna um post específico com base no ID fornecido, independente se publicado ou não. Apenas usuários com role ADMIN podem acessar este endpoint.")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post localizado",
                content = @Content(schema = @Schema(implementation = AdminPostResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Post não encontrado ou já excluído",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Post inexistente ou não disponível.\", \"path\": \"/admin/posts/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Token de autenticação ausente ou inválido.\", \"path\": \"/admin/posts/1\"}"))),
            @ApiResponse(responseCode = "403", description = "Acesso negado: requer perfil ADMIN",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 403, \"error\": \"Forbidden\", \"message\": \"Você não possui autorização para acessar este recurso.\", \"path\": \"/admin/posts/1\"}")))
        })
        public ResponseEntity<AdminPostResponseDTO> findPostById(
            @Parameter(description = "ID do post", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(postService.findPostById(id));
    }

    @PostMapping
    @Operation(summary = "Criar um novo post", description = "Permite a criação de um novo post. Apenas usuários com role ADMIN podem acessar este endpoint.")
        @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Post criado com sucesso",
                content = @Content(schema = @Schema(implementation = PostResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, categoria/tipo de tecido inexistente ou texto alternativo ausente para a imagem de capa",
                content = @Content(schema = @Schema(anyOf = {ValidationErrorResponseDTO.class, ErrorResponseDTO.class}),
                    examples = {
                        @ExampleObject(name = "Erro de validação", value = "{\"timestamp\": \"2026-09-22T12:00:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Erro de validação nos campos enviados.\", \"path\": \"/admin/posts\", \"fieldErrors\": []}"),
                        @ExampleObject(name = "Erro de regra de negócio", value = "{\"timestamp\": \"2026-09-22T12:00:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Categoria informada não foi encontrada.\", \"path\": \"/admin/posts\"}")
                    })),
            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Token de autenticação ausente ou inválido.\", \"path\": \"/admin/posts\"}"))),
            @ApiResponse(responseCode = "403", description = "Acesso negado: requer perfil ADMIN",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 403, \"error\": \"Forbidden\", \"message\": \"Você não possui autorização para acessar este recurso.\", \"path\": \"/admin/posts\"}")))
        })
    public ResponseEntity<PostResponseDTO> createPost(
            @Valid @RequestBody PostCreateRequestDTO dto,
            @Parameter(hidden = true) @AuthenticationPrincipal User loggedInUser,
            UriComponentsBuilder uriBuilder) {
        PostResponseDTO responseDto = postService.createPost(dto, loggedInUser);

        URI uri = uriBuilder.path("/admin/posts/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();

        return ResponseEntity.created(uri).body(responseDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar um post existente", description = "Permite a atualização de um post existente. Apenas usuários com role ADMIN podem acessar este endpoint.")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = PostResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, post não encontrado ou excluído, ou categoria/tipo de tecido inexistente",
                content = @Content(schema = @Schema(anyOf = {ValidationErrorResponseDTO.class, ErrorResponseDTO.class}),
                    examples = {
                        @ExampleObject(name = "Erro de validação", value = "{\"timestamp\": \"2026-09-22T12:00:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Erro de validação nos campos enviados.\", \"path\": \"/admin/posts/1\", \"fieldErrors\": []}"),
                        @ExampleObject(name = "Erro de regra de negócio", value = "{\"timestamp\": \"2026-09-22T12:00:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Post inexistente.\", \"path\": \"/admin/posts/1\"}")
                    })),
            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Token de autenticação ausente ou inválido.\", \"path\": \"/admin/posts/1\"}"))),
            @ApiResponse(responseCode = "403", description = "Acesso negado: requer perfil ADMIN",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 403, \"error\": \"Forbidden\", \"message\": \"Você não possui autorização para acessar este recurso.\", \"path\": \"/admin/posts/1\"}")))
        })
    public ResponseEntity<PostResponseDTO> updatePost(
            @Parameter(description = "ID do post", example = "1") @PathVariable Long id,
            @Valid @RequestBody PostEditRequestDTO dto) {
        return ResponseEntity.ok(postService.updatePost(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um post existente", description = "Permite a exclusão de um post existente. O post é marcado como excluído em vez de ser removido completamente. Apenas usuários com role ADMIN podem acessar este endpoint.")
        @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Post excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Post não encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Post inexistente.\", \"path\": \"/admin/posts/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Token de autenticação ausente ou inválido.\", \"path\": \"/admin/posts/1\"}"))),
            @ApiResponse(responseCode = "403", description = "Acesso negado: requer perfil ADMIN",
                content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                    examples = @ExampleObject(value = "{\"timestamp\": \"2026-08-27T00:00:00Z\", \"status\": 403, \"error\": \"Forbidden\", \"message\": \"Você não possui autorização para acessar este recurso.\", \"path\": \"/admin/posts/1\"}")))
        })
        public ResponseEntity<Void> deletePost(
            @Parameter(description = "ID do post", example = "1")
            @PathVariable Long id,
        
            @Valid @RequestBody PostDeleteRequestDTO dto,
        
            @AuthenticationPrincipal User loggedInUser) {
        postService.deletePost(id, dto, loggedInUser);
        return ResponseEntity.noContent().build();
    }
}