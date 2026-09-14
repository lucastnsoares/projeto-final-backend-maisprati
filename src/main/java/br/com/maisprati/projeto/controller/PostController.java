package br.com.maisprati.projeto.controller;

import br.com.maisprati.projeto.dto.request.CategoryRequestDTO;
import br.com.maisprati.projeto.dto.request.PostCreateRequestDTO;
import br.com.maisprati.projeto.dto.response.CategoryResponseDTO;
import br.com.maisprati.projeto.dto.response.PostResponseDTO;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.service.CategoryService;
import br.com.maisprati.projeto.service.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PostController {
    private final PostService postService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody @Valid PostCreateRequestDTO dto, @AuthenticationPrincipal User loggedInUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(dto, loggedInUser))  ;
    }

    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> findAllPosts(
        @ParameterObject
        @PageableDefault(page = 0, size = 10, sort = "title", direction = Sort.Direction.ASC)
        Pageable pageable) {
        return ResponseEntity.ok(postService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> findPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findPostById(id));
    }
}
