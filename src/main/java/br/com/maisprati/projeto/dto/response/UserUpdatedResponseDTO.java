package br.com.maisprati.projeto.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de atualização de perfil contendo os dados cadastrais e o token JWT atualizado")
public record UserUpdatedResponseDTO(
        @Schema(description = "Dados cadastrais atualizados do usuário")
        UserResponseDTO user,

        @Schema(description = "Novo token JWT emitido com as alterações", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {}
