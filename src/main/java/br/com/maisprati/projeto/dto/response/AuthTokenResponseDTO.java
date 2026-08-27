package br.com.maisprati.projeto.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token JWT emitido após autenticação bem-sucedida")
public record AuthTokenResponseDTO(
        @Schema(description = "Token JWT de acesso", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {}
