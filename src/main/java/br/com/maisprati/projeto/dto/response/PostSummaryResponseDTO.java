package br.com.maisprati.projeto.dto.response;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.maisprati.projeto.model.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;

public record PostSummaryResponseDTO(
    @Schema(description = "Identificador único do conteúdo", example = "1")
        Long id,

        @Schema(description = "Slug do artigo", example = "como-reaproveitar-retalhos-de-jeans")
        String slug,

        @Schema(description = "Título do artigo", example = "Como reaproveitar retalhos de jeans")
        String title,

        @Schema(description = "Resumo introdutório em linguagem simples e acessível", example = "Aprenda técnicas práticas para transformar sobras de tecido jeans em ecobags e organizadores.")
        String summary,

        @Schema(description = "Nome da categoria associada ao artigo", example = "Upcycling")
        String categoryName,

        @Schema(description = "URL da imagem de capa", example = "https://www.projeto.com.br/posts/jeans-upcycling.jpg")
        String coverImageUrl,

        @Schema(description = "Texto alternativo da imagem para leitores de tela e acessibilidade (WCAG)", example = "Mãos costurando retalhos de calça jeans sobre uma mesa de madeira.")
        String coverImageAlt,

        @Schema(description = "Palavras-chave livres associadas ao conteúdo", example = "[\"upcycling\", \"jeans\", \"diy\"]")
        Set<String> tags,

        @Schema(description = "Tipos de tecido aceitos que se relacionam com este artigo", example = "[{\"id\":1,\"name\":\"Algodão\"},{\"id\":3,\"name\":\"Jeans\"}]")
        Set<ClothTypeSummaryResponseDTO> clothTypes,

        @Schema(description = "Data de criação do registro", example = "2026-08-26T22:00:00Z")
        Instant createdAt
) {
    public PostSummaryResponseDTO(Post post) {
        this(
            post.getId(), 
            post.getSlug(),
            post.getTitle(),
            post.getSummary(),
            post.getCategory() != null ? post.getCategory().getName() : "",
            post.getCoverImageUrl() != null ? post.getCoverImageUrl() : "",
            post.getCoverImageAlt() != null ? post.getCoverImageAlt() : "",
            post.getTags(),
            post.getClothTypes() != null ? post.getClothTypes().stream()
                .map(ClothTypeSummaryResponseDTO::new)
                .collect(Collectors.toSet()) : Collections.emptySet(),
            post.getCreatedAt()
        );
    }
}
