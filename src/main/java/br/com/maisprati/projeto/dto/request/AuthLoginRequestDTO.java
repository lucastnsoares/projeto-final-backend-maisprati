package br.com.maisprati.projeto.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados necessários para autenticação e login")
public record AuthLoginRequestDTO(
        @Schema(description = "E-mail do usuário", example = "usuario@email.com")
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        @Schema(description = "Senha cadastrada do usuário", example = "Senha@123")
        @NotBlank(message = "A senha é obrigatória.")
        String password
) {}
