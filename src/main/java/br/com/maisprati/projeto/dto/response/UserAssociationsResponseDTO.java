package br.com.maisprati.projeto.dto.response;

import java.util.Set;

public record UserAssociationsResponseDTO(
	Long userId,
    Set<AssociationResponseDTO> managerInCollectionPoints,
    Set<AssociationResponseDTO> operatorInCollectionPoints
) {

	public UserAssociationsResponseDTO(Long userId, Set<AssociationResponseDTO> managerInCollectionPoints, Set<AssociationResponseDTO> operatorInCollectionPoints) {
		this.userId = userId;
		this.managerInCollectionPoints = managerInCollectionPoints;
		this.operatorInCollectionPoints = operatorInCollectionPoints;
	}

}
