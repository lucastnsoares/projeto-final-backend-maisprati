package br.com.maisprati.projeto.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ClothTypeRequestDTO(
        @NotBlank
        @Schema(description = "Nome do tecido", example = "Jeans")
        String name,

        @NotBlank
        @Schema(description = "Descrição do tecido", example = "Material têxtil composto majoritariamente por fibras de algodão com ligamento em sarja (Denim).")
        String description
) {

}
