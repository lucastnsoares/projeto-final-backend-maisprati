package br.com.maisprati.projeto.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalhe da inconsistência de um campo")
public record FieldErrorDTO(
        @Schema(description = "Nome do atributo inválido")
        String field,

        @Schema(description = "Mensagem de validação do campo")
        String message
) {}
