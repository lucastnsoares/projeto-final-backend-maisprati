package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.entity.ClothType;

public record ClothTypeSummaryResponseDTO(
        Long id,
        String name
) {
    public ClothTypeSummaryResponseDTO(ClothType clothType) {
        this(clothType.getId(), clothType.getName());
    }
}