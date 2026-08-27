package br.com.maisprati.projeto.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "Estrutura de erro retornada em falhas de validação de campos (@Valid)")
public record ValidationErrorResponseDTO(
        @Schema(description = "Data e hora do erro", example = "2026-08-27T00:00:00Z")
        Instant timestamp,

        @Schema(description = "Código de status HTTP", example = "400")
        int status,

        @Schema(description = "Descrição do status HTTP", example = "Bad Request")
        String error,

        @Schema(description = "Mensagem geral", example = "Erro de validação nos campos enviados.")
        String message,

        @Schema(description = "URI do recurso acessado")
        String path,

        @Schema(description = "Lista detalhada de inconsistências por campo")
        List<FieldErrorDTO> fieldErrors
) {}