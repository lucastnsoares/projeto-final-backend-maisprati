package br.com.maisprati.projeto.dto.request;

import br.com.maisprati.projeto.validation.InternationalPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Campos permitidos para edição de perfil pelo próprio usuário")
public record UserEditProfileRequestDTO(
        @Schema(description = "Nome completo do usuário", example = "JOÃO DA SILVA SANTOS")
        @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres.")
        String name,

        @Schema(description = "Novo e-mail de acesso", example = "joao.novo@email.com")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        @Schema(description = "Telefone no padrão internacional E.164", example = "+5531988887777")
        @InternationalPhone(message = "Número de telefone inválido no padrão internacional.")
        String phone
) {}
