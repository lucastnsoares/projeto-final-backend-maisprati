package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.entity.Category;

public record CategoryResponseDTO(
        Long id,
        String name
) {
    public CategoryResponseDTO(Category category) {
        this(
                category.getId(),
                category.getName()
        );
    }
}
