package br.com.maisprati.projeto.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO (
        @NotBlank
        @Size(min = 3, max = 50, message = "O nome da categoria deve possuir entre 3 e 50 caracteres")
        String name
){
}
