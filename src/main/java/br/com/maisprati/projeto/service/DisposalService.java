package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.DisposalCreateRequestDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointDisposalsSummaryDTO;
import br.com.maisprati.projeto.dto.response.DisposalResponseDTO;
import br.com.maisprati.projeto.model.entity.ClothType;
import br.com.maisprati.projeto.model.entity.CollectionPoint;
import br.com.maisprati.projeto.model.entity.Disposal;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.CollectionPointStatus;
import br.com.maisprati.projeto.model.enums.DisposalStatus;
import br.com.maisprati.projeto.model.enums.Role;
import br.com.maisprati.projeto.repository.ClothTypeRepository;
import br.com.maisprati.projeto.repository.CollectionPointRepository;
import br.com.maisprati.projeto.repository.DisposalRepository;
import br.com.maisprati.projeto.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisposalService {

    private final DisposalRepository disposalRepository;
    private final UserRepository userRepository;
    private final CollectionPointRepository collectionPointRepository;
    private final ClothTypeRepository clothTypeRepository;

    @Transactional
    public DisposalResponseDTO registerDisposal(DisposalCreateRequestDTO dto, String username) {
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        CollectionPoint point = collectionPointRepository.findById(dto.collectionPointId())
                .orElseThrow(() -> new EntityNotFoundException("Ponto de coleta não encontrado."));

        // 1. Validação de segurança: apenas ADMIN, GERENTE do ponto ou OPERADOR do ponto
        boolean isAdmin = currentUser.getRole().contains(Role.ADMIN);
        boolean isManager = point.getManagers().stream().anyMatch(m -> m.getId().equals(currentUser.getId()));
        boolean isOperator = point.getOperators().stream().anyMatch(o -> o.getId().equals(currentUser.getId()));

        if (!isAdmin && !isManager && !isOperator) {
            throw new AccessDeniedException("Você não tem permissão para registrar um descarte neste ponto de coleta.");
        }

        if (point.getStatus() != CollectionPointStatus.ACTIVE) {
            throw new IllegalStateException("Este ponto de coleta não está recebendo doações no momento.");
        }

        List<ClothType> clothTypes = clothTypeRepository.findAllById(dto.clothTypeIds());
        if (clothTypes.isEmpty() || clothTypes.size() != dto.clothTypeIds().size()) {
            throw new IllegalArgumentException("Um ou mais tipos de tecidos informados não foram encontrados.");
        }

        Disposal disposal = Disposal.builder()
                .user(currentUser) // Quem está registrando
                .collectionPoint(point)
                .clothTypes(new HashSet<>(clothTypes))
                .approximateWeightInKg(dto.approximateWeightInKg())
                .status(DisposalStatus.PENDING)
                .build();

        Disposal saved = disposalRepository.save(disposal);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public CollectionPointDisposalsSummaryDTO getDisposalsByCollectionPoint(Long collectionPointId, String username, Pageable pageable) {
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        CollectionPoint point = collectionPointRepository.findById(collectionPointId)
                .orElseThrow(() -> new EntityNotFoundException("Ponto de coleta não encontrado."));

        // Validação de segurança idêntica para o GET
        boolean isAdmin = currentUser.getRole().contains(Role.ADMIN);
        boolean isManager = point.getManagers().stream().anyMatch(m -> m.getId().equals(currentUser.getId()));
        boolean isOperator = point.getOperators().stream().anyMatch(o -> o.getId().equals(currentUser.getId()));

        if (!isAdmin && !isManager && !isOperator) {
            throw new AccessDeniedException("Você não tem permissão para visualizar os descartes deste ponto de coleta.");
        }

        Page<DisposalResponseDTO> disposalsPage = disposalRepository.findByCollectionPointId(collectionPointId, pageable)
                .map(this::mapToDTO);

        BigDecimal totalWeight = disposalRepository.getTotalWeightByCollectionPointId(collectionPointId);

        return new CollectionPointDisposalsSummaryDTO(totalWeight, disposalsPage);
    }

    private DisposalResponseDTO mapToDTO(Disposal entity) {
        return DisposalResponseDTO.builder()
                .id(entity.getId())
                .userName(entity.getUser().getName())
                .collectionPointName(entity.getCollectionPoint().getName())
                .approximateWeightInKg(entity.getApproximateWeightInKg())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .clothTypes(entity.getClothTypes().stream()
                        .map(ClothType::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}