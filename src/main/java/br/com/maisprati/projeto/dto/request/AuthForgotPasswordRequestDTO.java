package br.com.maisprati.projeto.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para solicitação de redefinição de senha")
public record AuthForgotPasswordRequestDTO(
        @Schema(description = "E-mail cadastrado na conta", example = "usuario@email.com")
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        String email
) {}
