package br.com.maisprati.projeto.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "Dados para criação de um novo conteúdo educativo sobre reaproveitamento têxtil")
public record PostCreateRequestDTO(
        @Schema(description = "Título do artigo", example = "Como reaproveitar retalhos de jeans")
        @NotBlank(message = "O título é obrigatório.")
        @Size(min = 5, max = 150, message = "O título deve ter entre 5 e 150 caracteres.")
        String title,

        @Schema(description = "Resumo introdutório em linguagem simples e acessível", example = "Aprenda técnicas práticas para transformar sobras de tecido jeans em ecobags e organizadores.")
        @NotBlank(message = "O resumo é obrigatório.")
        String summary,

        @Schema(description = "Conteúdo completo do artigo (Markdown ou texto estruturado)", example = "## Materiais necessários\n- Retalhos de jeans\n- Linha e agulha...")
        @NotBlank(message = "O corpo do artigo é obrigatório.")
        String body,

        @Schema(description = "ID da categoria associada ao artigo", example = "1")
        @NotNull(message = "O ID da categoria é obrigatório.")
        Long categoryId,

        @Schema(description = "URL da imagem de capa", example = "https://www.projeto.com.br/posts/jeans-upcycling.jpg")
        @Size(max = 2048, message = "A URL da imagem não pode ultrapassar 2048 caracteres.")
        String coverImageUrl,

        @Schema(description = "Texto alternativo da imagem para leitores de tela e acessibilidade (WCAG)", example = "Mãos costurando retalhos de calça jeans sobre uma mesa de madeira.")
        @Size(max = 255, message = "O texto alternativo não pode ultrapassar 255 caracteres.")
        String coverImageAlt,

        @Schema(description = "Palavras-chave livres associadas ao conteúdo", example = "[\"upcycling\", \"jeans\", \"diy\"]")
        Set<@Size(max = 30, message = "Cada tag deve ter no máximo 30 caracteres.") String> tags,

        @Schema(description = "IDs dos tipos de tecido aceitos que se relacionam com este artigo", example = "[1, 3]")
        Set<Long> clothTypeIds
) {
}
