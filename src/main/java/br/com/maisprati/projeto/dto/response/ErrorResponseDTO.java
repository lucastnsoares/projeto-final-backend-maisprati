package br.com.maisprati.projeto.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Estrutura padrão de erro da API")
public record ErrorResponseDTO(
        @Schema(description = "Data e hora do erro", example = "2026-08-27T00:00:00Z")
        Instant timestamp,

        @Schema(description = "Código de status HTTP")
        int status,

        @Schema(description = "Descrição do status HTTP")
        String error,

        @Schema(description = "Mensagem explicativa do erro")
        String message,

        @Schema(description = "URI do recurso acessado")
        String path
) {}