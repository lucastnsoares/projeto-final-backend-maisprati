package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.entity.CollectionPoint;
import br.com.maisprati.projeto.model.enums.CollectionPointStatus;

public record AssociationResponseDTO(
    Long id,
    String name,
    CollectionPointStatus status
) {

    public AssociationResponseDTO(CollectionPoint entity) {
        this(
            entity.getId(),
            entity.getName(),
            entity.getStatus()
        );
    }
}
