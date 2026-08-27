package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Resumo cadastral simplificado do usuário")
public record UserSummaryResponseDTO(
        @Schema(description = "Nome do usuário", example = "JOÃO DA SILVA")
        String name,

        @Schema(description = "Documento sanitizado", example = "12345678909")
        String document,

        @Schema(description = "E-mail cadastrado", example = "joao.silva@email.com")
        String email,

        @Schema(description = "Telefone no padrão E.164", example = "+5531987654321")
        String phone,

        @Schema(description = "Data de cadastro", example = "2026-08-26T22:00:00Z")
        Instant createdAt
) {
    public UserSummaryResponseDTO(User user) {
        this(
                user.getName(),
                user.getDocument(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt());
    }
}
