package br.com.maisprati.projeto.dto.response;

import java.math.BigDecimal;

import br.com.maisprati.projeto.dto.projection.CollectionPointDistanceProjectionDTO;

public record CollectionPointDistanceResponseDTO(
        Long id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        Double distanceKm
) {
    public CollectionPointDistanceResponseDTO(CollectionPointDistanceProjectionDTO projection) {
        this(
                projection.getId(),
                projection.getName(),
                projection.getAddress(),
                projection.getLatitude(),
                projection.getLongitude(),
                Math.round(projection.getDistanceKm() * 100.0) / 100.0
        );
    }
}