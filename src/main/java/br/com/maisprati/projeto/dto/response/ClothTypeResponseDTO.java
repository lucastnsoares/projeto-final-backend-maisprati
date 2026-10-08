package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.entity.ClothType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ClothTypeResponseDTO(
        @Schema(description = "Identificador único", example = "1")
        Long id,

        @Schema(description = "Nome do tecido", example = "Jeans")
        String name,

        @Schema(description = "Descrição do tecido", example = "Material têxtil composto majoritariamente por fibras de algodão com ligamento em sarja (Denim).")
        String description,

        @Schema(description = "Informação de disponibilidade do tipo de tecido", example = "true")
        Boolean isActive
) {
    public ClothTypeResponseDTO(ClothType clothType) {
        this(
                clothType.getId(),
                clothType.getName(),
                clothType.getDescription(),
                clothType.isActive()
        );
    }
}
