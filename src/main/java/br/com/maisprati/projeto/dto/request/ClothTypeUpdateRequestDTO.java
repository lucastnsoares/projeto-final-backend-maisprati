package br.com.maisprati.projeto.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record ClothTypeUpdateRequestDTO(
        @Schema(description = "Nome do tecido", example = "Jeans")
        @Size(min = 2, max = 30, message = "O nome do tecido deve ter entre 2 e 30 caracteres.")
        String name,

        @Schema(description = "Descrição do tecido", example = "Material têxtil composto majoritariamente por fibras de algodão com ligamento em sarja (Denim).")
        @Size(max = 255, message = "A descrição não pode ultrapassar 255 caracteres.")
        String description,

        @Schema(description = "Informação de disponibilidade do tipo de tecido", example = "true")
        Boolean isActive
) {
}
