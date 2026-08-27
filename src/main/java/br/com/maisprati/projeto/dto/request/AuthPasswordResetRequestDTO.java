package br.com.maisprati.projeto.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para confirmação e definição de nova senha via token")
public record AuthPasswordResetRequestDTO(
        @Schema(description = "Token UUID recebido no e-mail", example = "c8b417e2-7634-4062-81f1-3cf93f9ef21b")
        @NotBlank(message = "O token é obrigatório.")
        String token,

        @Schema(description = "Nova senha de acesso (mínimo 6 caracteres)", example = "NovaSenha@123")
        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.")
        String newPassword
) {}
