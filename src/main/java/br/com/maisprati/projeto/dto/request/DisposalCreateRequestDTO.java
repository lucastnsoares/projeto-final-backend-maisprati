package br.com.maisprati.projeto.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record DisposalCreateRequestDTO(
    @NotNull(message = "O ponto de coleta é obrigatório.")
    Long collectionPointId,

    @Positive(message = "O peso deve ser maior que zero.")
    BigDecimal approximateWeightInKg,

    @NotEmpty(message = "Informe ao menos um tipo de tecido que está sendo descartado.")
    List<Long> clothTypeIds
) {
}
