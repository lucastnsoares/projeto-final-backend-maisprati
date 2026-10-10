package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.enums.DisposalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Builder
@Schema(description = "Dados de resposta do registro de descarte")
public record DisposalResponseDTO(

        @Schema(description = "ID do registro de descarte", example = "1")
        Long id,

        @Schema(description = "Nome do doador", example = "Luis Carlos")
        String userName,

        @Schema(description = "Nome do ponto de coleta onde o descarte foi feito", example = "EcoPonto Central")
        String collectionPointName,

        @Schema(description = "Peso aproximado informado pelo doador", example = "2.50")
        BigDecimal approximateWeightInKg,

        @Schema(description = "Tipos de tecidos descartados")
        Set<String> clothTypes,

        @Schema(description = "Status atual do descarte", example = "PENDING")
        DisposalStatus status,

        @Schema(description = "Data e hora do registro")
        Instant createdAt
) {
}