package br.com.maisprati.projeto.dto.response;
import br.com.maisprati.projeto.model.entity.Post;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public record PostResponseDTO(
        Long id,

        String slug,

        String title,

        String summary,

        String body,

        String categoryName,

        String coverImageUrl,

        String coverImageAlt,

        Set<String> tags,

        Set<ClothTypeSummaryResponseDTO> clothTypes,

        Instant createdAt,

        Instant updatedAt,

        String createdByName
) {
    public PostResponseDTO(Post post) {
        this(
                post.getId(),
                post.getSlug(),
                post.getTitle(),
                post.getSummary(),
                post.getBody(),
                post.getCategory() != null ? post.getCategory().getName() : null,
                post.getCoverImageUrl(),
                post.getCoverImageAlt(),
                post.getTags(),
                post.getClothTypes() != null
                        ? post.getClothTypes().stream().map(ClothTypeSummaryResponseDTO::new).collect(Collectors.toSet())
                        : Collections.emptySet(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getCreatedBy() != null ? post.getCreatedBy().getName() : null
        );
    }
}
