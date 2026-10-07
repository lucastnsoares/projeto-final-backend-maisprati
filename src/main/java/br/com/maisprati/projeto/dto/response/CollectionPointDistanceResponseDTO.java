package br.com.maisprati.projeto.dto.response;

import java.math.BigDecimal;
import java.util.List;

import br.com.maisprati.projeto.dto.projection.CollectionPointDistanceProjectionDTO;

public record CollectionPointDistanceResponseDTO(
        Long id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        Double distanceKm,
        List<String> acceptedClothTypes
) {
    public CollectionPointDistanceResponseDTO(CollectionPointDistanceProjectionDTO projection, List<String> clothTypesNamesList) {
        this(
                projection.getId(),
                projection.getName(),
                projection.getAddress(),
                projection.getLatitude(),
                projection.getLongitude(),
                Math.round(projection.getDistanceKm() * 100.0) / 100.0,
                clothTypesNamesList
        );
    }
}