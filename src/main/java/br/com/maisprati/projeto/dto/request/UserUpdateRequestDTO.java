package br.com.maisprati.projeto.dto.request;

import br.com.maisprati.projeto.model.enums.Role;
import br.com.maisprati.projeto.validation.CpfOrCnpj;
import br.com.maisprati.projeto.validation.InternationalPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "Campos para atualização administrativa de um usuário")
public record UserUpdateRequestDTO(
        @Schema(description = "Nome completo", example = "CARLOS ALBERTO")
        @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres.")
        String name,

        @Schema(description = "CPF ou CNPJ", example = "52998224725")
        @CpfOrCnpj(message = "Documento inválido. Informe um CPF ou CNPJ válido.")
        String document,

        @Schema(description = "E-mail de acesso", example = "carlos.admin@email.com")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        @Schema(description = "Telefone internacional E.164", example = "+5531977778888")
        @InternationalPhone(message = "Número de telefone inválido no padrão internacional.")
        String phone,

        @Schema(description = "Novos perfis do usuário", example = "[\"DOADOR\"]")
        Set<Role> roles,

        @Schema(description = "Status de ativação da conta", example = "true")
        Boolean isActive
) {}
