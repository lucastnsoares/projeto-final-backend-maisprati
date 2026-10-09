package br.com.maisprati.projeto.dto.response;
import br.com.maisprati.projeto.model.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public record PostResponseDTO(
        @Schema(description = "Identificador único do conteúdo", example = "1")
        Long id,

        @Schema(description = "Slug do artigo", example = "como-reaproveitar-retalhos-de-jeans")
        String slug,

        @Schema(description = "Título do artigo", example = "Como reaproveitar retalhos de jeans")
        String title,

        @Schema(description = "Resumo introdutório em linguagem simples e acessível", example = "Aprenda técnicas práticas para transformar sobras de tecido jeans em ecobags e organizadores.")
        String summary,

        @Schema(description = "Conteúdo completo do artigo (Markdown ou texto estruturado)", example = "## Materiais necessários\n- Retalhos de jeans\n- Linha e agulha...")
        String body,

        @Schema(description = "Categoria associada ao artigo", example = "{\"id\":1,\"name\":\"Upcycling\"}")
        CategoryResponseDTO category,

        @Schema(description = "URL da imagem de capa", example = "https://www.projeto.com.br/posts/jeans-upcycling.jpg")
        String coverImageUrl,

        @Schema(description = "Texto alternativo da imagem para leitores de tela e acessibilidade (WCAG)", example = "Mãos costurando retalhos de calça jeans sobre uma mesa de madeira.")
        String coverImageAlt,

        @Schema(description = "Palavras-chave livres associadas ao conteúdo", example = "[\"upcycling\", \"jeans\", \"diy\"]")
        Set<String> tags,

        @Schema(description = "Tipos de tecido aceitos que se relacionam com este artigo", example = "[{\"id\":1,\"name\":\"Algodão\"},{\"id\":3,\"name\":\"Jeans\"}]")
        Set<ClothTypeSummaryResponseDTO> clothTypes,

        @Schema(description = "Data de criação do registro", example = "2026-08-26T22:00:00Z")
        Instant createdAt,

        @Schema(description = "Data de atualização do registro", example = "2026-08-26T22:00:00Z")
        Instant updatedAt,

        @Schema(description = "Nome do usuário que criou o conteúdo", example = "João da Silva")
        String createdByName,

        @Schema(description = "Indica se o artigo está publicado ou não", example = "true")
        Boolean isPublished
) {
    public PostResponseDTO(Post post) {
        this(
                post.getId(),
                post.getSlug(),
                post.getTitle(),
                post.getSummary(),
                post.getBody(),
                post.getCategory() != null ? new CategoryResponseDTO(post.getCategory()) : null,
                post.getCoverImageUrl(),
                post.getCoverImageAlt(),
                post.getTags(),
                post.getClothTypes() != null
                        ? post.getClothTypes().stream().map(ClothTypeSummaryResponseDTO::new).collect(Collectors.toSet())
                        : Collections.emptySet(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getCreatedBy() != null ? post.getCreatedBy().getName() : null,
                post.isPublished()
        );
    }
}
