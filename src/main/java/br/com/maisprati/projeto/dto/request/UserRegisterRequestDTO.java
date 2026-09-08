package br.com.maisprati.projeto.dto.request;

import br.com.maisprati.projeto.validation.CpfOrCnpj;
import br.com.maisprati.projeto.validation.InternationalPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para auto-cadastro público de novos usuários (Doador)")
public record UserRegisterRequestDTO(
        @Schema(description = "Nome completo do usuário", example = "JOÃO DA SILVA")
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres.")
        String name,

        @Schema(description = "CPF (11 dígitos) ou CNPJ Alfanumérico (14 caracteres)", example = "12345678909")
        @NotBlank(message = "O documento é obrigatório.")
        @CpfOrCnpj(message = "Documento inválido. Informe um CPF ou CNPJ válido.")
        String document,

        @Schema(description = "E-mail de acesso", example = "joao.silva@email.com")
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        @Schema(description = "Senha de acesso (mínimo 6 caracteres)", example = "Senha@123")
        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        String password,

        @Schema(description = "Telefone no padrão internacional E.164", example = "+5531987654321")
        @NotBlank(message = "O telefone é obrigatório.")
        @InternationalPhone(message = "Número de telefone inválido no padrão internacional.")
        String phone
) {}
