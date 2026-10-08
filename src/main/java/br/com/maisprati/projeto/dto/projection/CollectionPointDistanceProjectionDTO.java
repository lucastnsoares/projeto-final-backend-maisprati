package br.com.maisprati.projeto.dto.projection;

import java.math.BigDecimal;

public interface CollectionPointDistanceProjectionDTO {
    Long getId();
    String getName();
    String getAddress();
    BigDecimal getLatitude();
    BigDecimal getLongitude();
    Double getDistanceKm();
}
