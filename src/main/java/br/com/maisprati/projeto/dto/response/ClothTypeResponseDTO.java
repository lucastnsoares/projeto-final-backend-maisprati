package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.entity.ClothType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ClothTypeResponseDTO(
        @Schema(description = "Identificador único", example = "1")
        Long id,

        @Schema(description = "Nome do tecido", example = "Jeans")
        String name,

        @Schema(description = "Descrição do tecido", example = "Jeans")
        String description
) {
    public ClothTypeResponseDTO(ClothType clothType) {
        this(
                clothType.getId(),
                clothType.getName(),
                clothType.getDescription()
        );
    }
}
