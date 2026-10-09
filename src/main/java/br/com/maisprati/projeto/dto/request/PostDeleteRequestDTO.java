package br.com.maisprati.projeto.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostDeleteRequestDTO(
        @Size(min = 10, max = 255, message = "A justificativa deve ter entre 10 e 255 caracteres.")
        @NotBlank (message = "A justificativa é obrigatória.")
        String justification
) {

}
