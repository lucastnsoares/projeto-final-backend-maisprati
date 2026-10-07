package br.com.maisprati.projeto.dto.request;

import br.com.maisprati.projeto.validation.CpfOrCnpj;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record OperatorCreateRequestDTO(

        @Schema(description = "CPF (11 dígitos) ou CNPJ Alfanumérico (14 caracteres)", example = "52998224725")
        @NotBlank(message = "O documento é obrigatório.")
        @CpfOrCnpj(message = "Documento inválido. Informe um CPF ou CNPJ válido.")
        String document
) {
    
}
