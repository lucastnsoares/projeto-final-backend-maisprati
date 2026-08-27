package br.com.maisprati.projeto.dto.request;

import br.com.maisprati.projeto.model.enums.Role;
import br.com.maisprati.projeto.validation.CpfOrCnpj;
import br.com.maisprati.projeto.validation.InternationalPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "Dados para criação de usuário por um Administrador")
public record UserCreateRequestDTO(
        @Schema(description = "Nome completo do usuário", example = "MARIA OLIVEIRA")
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres.")
        String name,

        @Schema(description = "CPF (11 dígitos) ou CNPJ Alfanumérico (14 caracteres)", example = "52998224725")
        @NotBlank(message = "O documento é obrigatório.")
        @CpfOrCnpj(message = "Documento inválido. Informe um CPF ou CNPJ válido.")
        String document,

        @Schema(description = "E-mail institucional/pessoal", example = "maria.oliveira@email.com")
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        @Schema(description = "Telefone no padrão internacional E.164", example = "+5531998765432")
        @NotBlank(message = "O telefone é obrigatório.")
        @InternationalPhone(message = "Número de telefone inválido no padrão internacional.")
        String phone,

        @Schema(description = "Perfis/permissões atribuídas ao usuário", example = "[\"ADMIN\", \"PONTO_COLETA_GERENTE\"]")
        @NotEmpty(message = "Pelo menos um perfil (role) deve ser informado.")
        Set<Role> roles
) {}
