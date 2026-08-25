package br.com.maisprati.projeto.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados necessários para realização de login")
public record LoginRequestDTO (
        @Schema(description = "E-mail do usuário", example = "user@email.com")
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        @Schema(description = "Senha do usuário", example = "password123")
        @NotBlank(message = "A senha é obrigatória.")
        String password

){ }
