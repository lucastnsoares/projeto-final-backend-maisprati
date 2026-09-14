package br.com.maisprati.projeto.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import br.com.maisprati.projeto.dto.request.PostCreateRequestDTO;
import br.com.maisprati.projeto.dto.request.PostEditRequestDTO;
import br.com.maisprati.projeto.dto.response.PostResponseDTO;
import br.com.maisprati.projeto.dto.response.PostSummaryResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/posts")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor 
@Tag(name = "Administração de Conteúdo", description = "Gerenciamento e controle de posts para administradores")
public class AdminPostController {
    private final PostService postService;

    @GetMapping
    @Operation(summary = "Listar todos os posts existentes de forma resumida e paginada", description = "Retorna os posts existentes de forma resumida. Apenas usuários com role ADMIN podem acessar este endpoint.")
    public ResponseEntity<Page<PostSummaryResponseDTO>> findAllPosts(
        @Parameter(description = "ID da categoria para filtrar os posts. Se não informado, retorna posts de todas as categorias.")
        @RequestParam(required = false) Long categoryId,
        
        @ParameterObject
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC)
        Pageable pageable) {
        return ResponseEntity.ok(postService.findAllPosts(categoryId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um post específico por ID", description = "Retorna um post específico com base no ID fornecido, independente se publicado ou não. Apenas usuários com role ADMIN podem acessar este endpoint.")
    public ResponseEntity<PostResponseDTO> findPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findPostById(id));
    }

    @PostMapping
    @Operation(summary = "Criar um novo post", description = "Permite a criação de um novo post. Apenas usuários com role ADMIN podem acessar este endpoint.")
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody @Valid PostCreateRequestDTO dto, @AuthenticationPrincipal User loggedInUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(dto, loggedInUser))  ;
    }

    @PatchMapping ("/{id}")
    @Operation(summary = "Atualizar um post existente", description = "Permite a atualização de um post existente. Apenas usuários com role ADMIN podem acessar este endpoint.")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @RequestBody @Valid PostEditRequestDTO dto) {
        return ResponseEntity.ok(postService.updatePost(id, dto));
    }
}
