package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.dto.request.CollectionPointUpdateDTO;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PendingUpdateResponseDTO {
    private Long id; // id do update
    private CollectionPointResponseDTO currentPoint; // Estado atual do ponto
    private CollectionPointUpdateDTO proposedChanges; // Dados propostos (Deserializados do JSON)
    private String requestedByEmail;
    private LocalDateTime requestedAt;
}