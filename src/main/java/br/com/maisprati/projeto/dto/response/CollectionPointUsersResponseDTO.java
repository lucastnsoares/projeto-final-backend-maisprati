package br.com.maisprati.projeto.dto.response;

import java.util.Set;

public record CollectionPointUsersResponseDTO(
    CollectionPointSummaryResponseDTO collectionPoint,
    Set<UserSummaryResponseDTO> managers,
    Set<UserSummaryResponseDTO> operators
) {

}
