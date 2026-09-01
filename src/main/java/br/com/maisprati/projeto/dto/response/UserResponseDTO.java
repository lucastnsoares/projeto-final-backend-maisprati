package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;

@Schema(description = "Detalhes completos do usuário")
public record UserResponseDTO(
        @Schema(description = "Identificador único", example = "1")
        Long id,

        @Schema(description = "Nome do usuário", example = "JOÃO DA SILVA")
        String name,

        @Schema(description = "Documento sanitizado", example = "12345678909")
        String document,

        @Schema(description = "E-mail cadastrado", example = "joao.silva@email.com")
        String email,

        @Schema(description = "Telefone formatado em E.164", example = "+5531987654321")
        String phone,

        @Schema(description = "Status do usuário", example = "true")
        Boolean active,

        @Schema(description = "Perfis associados ao usuário", example = "[\"DOADOR\"]")
        Set<Role> roles,

        @Schema(description = "Data de criação do registro", example = "2026-08-26T22:00:00Z")
        Instant createdAt,

        @Schema(description = "Data da última atualização", example = "2026-08-26T23:30:00Z")
        Instant updatedAt
) {

    public UserResponseDTO(User user){
        this(
                user.getId(),
                user.getName(),
                user.getDocument(),
                user.getEmail(),
                user.getPhone(),
                user.getActive(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

