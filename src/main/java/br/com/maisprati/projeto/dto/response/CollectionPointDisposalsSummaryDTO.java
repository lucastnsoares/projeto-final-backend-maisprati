package br.com.maisprati.projeto.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;

@Schema(description = "Resumo dos descartes de um ponto de coleta")
public record CollectionPointDisposalsSummaryDTO(

        @Schema(description = "Soma total do peso de todos os descartes registrados neste ponto (em kg)", example = "150.50")
        BigDecimal totalWeightInKg,

        @Schema(description = "Lista paginada dos itens descartados")
        Page<DisposalResponseDTO> disposals
) {}