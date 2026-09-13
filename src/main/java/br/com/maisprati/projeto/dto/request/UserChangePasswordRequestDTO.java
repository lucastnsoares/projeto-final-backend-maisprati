package br.com.maisprati.projeto.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para troca de senha do próprio usuário logado")
public record UserChangePasswordRequestDTO(
        @Schema(description = "Senha atual do usuário", example = "SenhaAtual@123")
        @NotBlank(message = "A senha atual é obrigatória.")
        String currentPassword,

        @Schema(description = "Nova senha desejada (mínimo 6 caracteres)", example = "NovaSenha@123")
        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.")
        String newPassword
) {}
