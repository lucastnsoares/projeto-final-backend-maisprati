package br.com.maisprati.projeto.dto.response;

import java.math.BigDecimal;

import br.com.maisprati.projeto.model.entity.CollectionPoint;

public record CollectionPointSummaryResponseDTO(
    Long id,
    String name,
    String status,
    String address,
    BigDecimal latitude,
    BigDecimal longitude
) {
    public CollectionPointSummaryResponseDTO(CollectionPoint entity) {
        this(
            entity.getId(),
            entity.getName(),
            entity.getStatus().toString(),
            formatAddress(entity),
            entity.getAddress().getLatitude(),
            entity.getAddress().getLongitude()
        );
    }

    private static String formatAddress(CollectionPoint entity) {
        if (entity.getAddress() == null) {
            return "";
        }
        String formattedAddress = entity.getAddress().getStreet() + ", " + entity.getAddress().getNumber();
        if (entity.getAddress().getComplement() != null && !entity.getAddress().getComplement().isEmpty()) {
            formattedAddress += " - " + entity.getAddress().getComplement();
        }
        formattedAddress += ", " + entity.getAddress().getNeighborhood() + ", " + entity.getAddress().getCity() + " - " + entity.getAddress().getState() + ", " + entity.getAddress().getCountry();
        return formattedAddress;
    }
    
}
