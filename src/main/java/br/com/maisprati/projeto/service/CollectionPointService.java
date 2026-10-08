package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.projection.CollectionPointDistanceProjectionDTO;
import br.com.maisprati.projeto.dto.request.CollectionPointCreateRequestDTO;
import br.com.maisprati.projeto.dto.request.CollectionPointUpdateDTO;
import br.com.maisprati.projeto.dto.request.OperatorCreateRequestDTO;
import br.com.maisprati.projeto.dto.response.*;
import br.com.maisprati.projeto.mapper.CollectionPointMapper;
import br.com.maisprati.projeto.model.entity.*;
import br.com.maisprati.projeto.model.enums.CollectionPointStatus;
import br.com.maisprati.projeto.model.enums.Role;
import br.com.maisprati.projeto.model.enums.State;
import br.com.maisprati.projeto.repository.ClothTypeRepository;
import br.com.maisprati.projeto.repository.CollectionPointPendingUpdateRepository;
import br.com.maisprati.projeto.repository.CollectionPointRepository;
import br.com.maisprati.projeto.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionPointService {

        private static final BigDecimal MIN_LATITUDE = new BigDecimal("-90.0");
        private static final BigDecimal MAX_LATITUDE = new BigDecimal("90.0");
        private static final BigDecimal MIN_LONGITUDE = new BigDecimal("-180.0");
        private static final BigDecimal MAX_LONGITUDE = new BigDecimal("180.0");

        private final CollectionPointRepository collectionPointRepository;
        private final CollectionPointPendingUpdateRepository pendingUpdateRepository;
        private final ClothTypeRepository clothTypeRepository;
        private final UserRepository userRepository;
        private final CollectionPointMapper collectionPointMapper;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Transactional
        public CollectionPointResponseDTO createCollectionPoint(CollectionPointCreateRequestDTO dto, String ownerEmail) {
                User owner = userRepository.findByEmail(ownerEmail)
                        .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

                List<ClothType> clothTypes = clothTypeRepository.findAllById(dto.getClothTypeIds());
                if (clothTypes.isEmpty()) {
                        throw new IllegalArgumentException("Informe ao menos um tipo de tecido válido cadastrado no sistema.");
                }

                if (clothTypes.size() != dto.getClothTypeIds().size()) {
                        throw new IllegalArgumentException("Um ou mais tipos de tecidos informados não foram encontrados.");
                }

                Address address = Address.builder()
                        .street(dto.getAddress().getStreet().trim().toUpperCase(Locale.ROOT))
                        .number(dto.getAddress().getNumber().trim().toUpperCase(Locale.ROOT))
                        .complement(dto.getAddress().getComplement() != null
                                ? dto.getAddress().getComplement().trim().toUpperCase(Locale.ROOT)
                                : null)
                        .neighborhood(dto.getAddress().getNeighborhood().trim().toUpperCase(Locale.ROOT))
                        .city(dto.getAddress().getCity().trim().toUpperCase(Locale.ROOT))
                        .state(State.valueOf(dto.getAddress().getState().trim().toUpperCase()))
                        .country(dto.getAddress().getCountry().trim().toUpperCase(Locale.ROOT))
                        .zipCode(dto.getAddress().getZipCode().trim())
                        .latitude(dto.getLatitude())
                        .longitude(dto.getLongitude())
                        .build();

                CollectionPoint collectionPoint = CollectionPoint.builder()
                        .name(dto.getName().trim().toUpperCase(Locale.ROOT))
                        .address(address)
                        .pointPictureUrl((dto.getImageUrl()) != null ? dto.getImageUrl().trim() : null)
                        .status(CollectionPointStatus.PENDING)
                        .managers(new HashSet<>())
                        .clothTypes(new HashSet<>())
                        .operatingHours(new HashSet<>())
                        .build();

                collectionPoint.getManagers().add(owner);
                collectionPoint.getClothTypes().addAll(clothTypes);

                if (dto.getOperatingHours() != null) {
                        var operatingHours = dto.getOperatingHours().stream().map(hDto -> {
                                OperatingHour hour = new OperatingHour();
                                hour.setDayOfWeek(java.time.DayOfWeek.valueOf(hDto.getDayOfWeek().toUpperCase()));
                                hour.setOpeningTime(java.time.LocalTime.parse(hDto.getOpenTime()));
                                hour.setClosingTime(java.time.LocalTime.parse(hDto.getCloseTime()));
                                return hour;
                        }).collect(Collectors.toSet());

                        collectionPoint.getOperatingHours().addAll(operatingHours);
                }
                collectionPoint.setHasPendingUpdate(false);
                collectionPoint.setStatus(CollectionPointStatus.PENDING); 

                CollectionPoint saved = collectionPointRepository.save(collectionPoint);
                return mapToDTO(saved);
        }

        @Transactional(readOnly = true)
        public Page<CollectionPointResponseDTO> findPendingCollectionPoints(Pageable pageable) {
                return collectionPointRepository.findByStatus(CollectionPointStatus.PENDING, pageable)
                        .map(this::mapToDTO);
        }

        @Transactional
        public CollectionPointResponseDTO approveCollectionPoint(Long id) {
                CollectionPoint point = collectionPointRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Ponto de coleta não encontrado com o ID: " + id));

                point.setStatus(CollectionPointStatus.ACTIVE);
                CollectionPoint updatedPoint = collectionPointRepository.save(point);
                return mapToDTO(updatedPoint);
        }

        @Transactional
        public CollectionPointResponseDTO rejectCollectionPoint(Long id) {
                CollectionPoint point = collectionPointRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Ponto de coleta não encontrado com o ID: " + id));

                point.setStatus(CollectionPointStatus.SUSPENDED);
                CollectionPoint updatedPoint = collectionPointRepository.save(point);
                return mapToDTO(updatedPoint);
        }

        @Transactional
        public CollectionPointResponseDTO pauseCollectionPoint(Long pointId, String username) {
                CollectionPoint point = collectionPointRepository.findById(pointId)
                        .orElseThrow(() -> new EntityNotFoundException("Ponto de coleta não encontrado com o ID: " + pointId));

                User currentUser = userRepository.findByEmail(username)
                        .orElseThrow(() -> new EntityNotFoundException("Usuário autenticado não encontrado."));

                boolean isAdmin = currentUser.getRole().contains(Role.ADMIN);
                boolean isManager = point.getManagers().stream()
                        .anyMatch(manager -> manager.getId().equals(currentUser.getId()));

                if (!isAdmin && !isManager) {
                        throw new AccessDeniedException("Você não possui autorização para acessar este recurso.");
                }

                point.setStatus(CollectionPointStatus.PAUSED);
                CollectionPoint updatedPoint = collectionPointRepository.save(point);

                return mapToDTO(updatedPoint);
        }

        @Transactional(readOnly = true)
        public Page<CollectionPointResponseDTO> findPublicApprovedPoints(Pageable pageable) {
                return collectionPointRepository.findByStatus(CollectionPointStatus.ACTIVE, pageable)
                        .map(this::mapToDTO);
        }

        @Transactional(readOnly = true)
        public CollectionPointResponseDTO getCollectionPointById(Long id) {
                CollectionPoint collectionPoint = collectionPointRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Ponto de coleta não encontrado."));
                return mapToDTO(collectionPoint);
        }

        @Transactional(readOnly = true)
        public CollectionPointResponseDTO getCollectionPointByIdAndStatusActive(Long id) {
                CollectionPoint collectionPoint = collectionPointRepository.findById(id)
                        .filter(point -> point.getStatus() == CollectionPointStatus.ACTIVE)
                        .orElseThrow(() -> new IllegalArgumentException("Ponto de coleta não encontrado ou indisponível."));
                return mapToDTO(collectionPoint);
        }

        @Transactional(readOnly = true)
        public Page<CollectionPointSummaryResponseDTO> findAll(Pageable pageable) {
                return bindClothTypesToSummaryResponseDTO(collectionPointRepository.findAll(pageable));
        }

        private Page<CollectionPointSummaryResponseDTO> bindClothTypesToSummaryResponseDTO(Page<CollectionPoint> page) {
                if (page.isEmpty()) {
                        return Page.empty(page.getPageable());
                }

                List<Long> ids = page.getContent().stream()
                        .map(CollectionPoint::getId)
                        .toList();

                List<Object[]> clothTypesData = collectionPointRepository.findClothTypesByCollectionPointIds(ids);

                Map<Long, List<String>> clothTypesMap = clothTypesData.stream()
                        .collect(Collectors.groupingBy(
                                row -> (Long) row[0],
                                Collectors.mapping(row -> (String) row[1], Collectors.toList())
                        ));

                return page.map(summary -> {
                        List<String> clothTypesNames = clothTypesMap.getOrDefault(summary.getId(), List.of());
                        return new CollectionPointSummaryResponseDTO(summary, clothTypesNames);
                });
        }

        @Transactional(readOnly = true)
        public Page<CollectionPointDistanceResponseDTO> findNearby(
                BigDecimal userLat,
                BigDecimal userLng,
                Double radiusKm,
                List<Long> clothTypeIds,
                Pageable pageable) {

                if (userLat.compareTo(MIN_LATITUDE) < 0 || userLat.compareTo(MAX_LATITUDE) > 0
                        || userLng.compareTo(MIN_LONGITUDE) < 0 || userLng.compareTo(MAX_LONGITUDE) > 0) {
                        throw new IllegalArgumentException("Coordenadas geográficas inválidas.");
                }

                BigDecimal effectiveRadius = (radiusKm != null && radiusKm > 0.0) ? new BigDecimal(radiusKm)
                        : new BigDecimal("15.0");

                if (clothTypeIds == null || clothTypeIds.isEmpty()) {
                        Page<CollectionPointDistanceProjectionDTO> projections = collectionPointRepository
                                .findNearbyNoClothTypes(userLat, userLng, effectiveRadius, pageable);
                        return bindClothTypesToResponseDTO(projections);
                } else {
                        Page<CollectionPointDistanceProjectionDTO> projections = collectionPointRepository
                                .findNearbyWithClothTypes(userLat, userLng, effectiveRadius, clothTypeIds, pageable);
                        return bindClothTypesToResponseDTO(projections);
                }
        }


        private Page<CollectionPointDistanceResponseDTO> bindClothTypesToResponseDTO(Page<CollectionPointDistanceProjectionDTO> page) {
                if (page.isEmpty()) {
                        return Page.empty(page.getPageable());
                }

                // Coleta apenas os IDs dos pontos de coleta retornados na página específica
                List<Long> ids = page.getContent().stream()
                        .map(CollectionPointDistanceProjectionDTO::getId)
                        .toList();

                // Faz a consulta em lote
                List<Object[]> clothTypesData = collectionPointRepository.findClothTypesByCollectionPointIds(ids);

                // Agrupa o resultado em um Map<IdDoPonto, List<NomeDoTecido>>
                Map<Long, List<String>> clothTypesMap = clothTypesData.stream()
                        .collect(Collectors.groupingBy(
                                row -> (Long) row[0],
                                Collectors.mapping(row -> (String) row[1], Collectors.toList())
                        ));

                return page.map(projection -> {
                        List<String> clothTypesNames = clothTypesMap.getOrDefault(projection.getId(), List.of());
                        return new CollectionPointDistanceResponseDTO(projection, clothTypesNames);

                });
        }

        @Transactional
        public CollectionPointResponseDTO updateCollectionPoint(Long id, CollectionPointUpdateDTO requestDTO, String username) {
                CollectionPoint point = collectionPointRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Ponto de coleta não encontrado."));

                User currentUser = userRepository.findByEmail(username)
                        .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

                boolean isAdmin = currentUser.getRole().contains(Role.ADMIN);
                boolean isManager = point.getManagers().stream().anyMatch(m -> m.getId().equals(currentUser.getId()));

                if (!isAdmin && !isManager) {
                        throw new AccessDeniedException("Você não tem permissão para editar este ponto de coleta.");
                }

                // gerente ou admin geram o update.
                try {
                        String payloadJson = objectMapper.writeValueAsString(requestDTO);

                        CollectionPointPendingUpdate pendingUpdate = pendingUpdateRepository.findByCollectionPointId(point.getId())
                                .orElse(CollectionPointPendingUpdate.builder().collectionPoint(point).build());

                        pendingUpdate.setUpdatePayloadJson(payloadJson);
                        pendingUpdate.setRequestedBy(currentUser);
                        pendingUpdate.setRequestedAt(LocalDateTime.now());
                        pendingUpdateRepository.save(pendingUpdate);

                        // Muda a flag para true, mas não sobe os dados editados
                        point.setHasPendingUpdate(true);
                        CollectionPoint savedPoint = collectionPointRepository.save(point);
                        collectionPointRepository.save(point);

                        return mapToDTO(savedPoint);
                } catch (JsonProcessingException e) {
                        throw new RuntimeException("Erro ao processar os dados da atualização.", e);
                }
        }

        @Transactional(readOnly = true)
        public Page<PendingUpdateResponseDTO> listPendingUpdates(Pageable pageable) {
                return pendingUpdateRepository.findAll(pageable).map(pending -> {
                        try {
                                CollectionPointUpdateDTO proposed = objectMapper.readValue(pending.getUpdatePayloadJson(), CollectionPointUpdateDTO.class);
                                return PendingUpdateResponseDTO.builder()
                                        .id(pending.getId())
                                        .currentPoint(mapToDTO(pending.getCollectionPoint()))
                                        .proposedChanges(proposed)
                                        .requestedByEmail(pending.getRequestedBy().getEmail())
                                        .requestedAt(pending.getRequestedAt())
                                        .build();
                        } catch (JsonProcessingException e) {
                                throw new RuntimeException("Erro ao ler dados pendentes.", e);
                        }
                });
        }

        @Transactional
        public CollectionPointResponseDTO approvePendingUpdate(Long collectionPointId) {
                CollectionPointPendingUpdate pending = pendingUpdateRepository.findByCollectionPointId(collectionPointId)
                        .orElseThrow(() -> new EntityNotFoundException("Nenhuma edição pendente para este ponto."));

                try {
                        CollectionPointUpdateDTO proposedDTO = objectMapper.readValue(pending.getUpdatePayloadJson(), CollectionPointUpdateDTO.class);
                        CollectionPoint point = pending.getCollectionPoint();

                        applyUpdatesToEntity(point, proposedDTO);
                        point.setHasPendingUpdate(false);

                        collectionPointRepository.save(point);
                        pendingUpdateRepository.delete(pending);

                        return mapToDTO(point);
                } catch (JsonProcessingException e) {
                        throw new RuntimeException("Erro ao processar os dados pendentes para aprovação.", e);
                }
        }

        @Transactional
        public void rejectPendingUpdate(Long collectionPointId) {
                CollectionPointPendingUpdate pending = pendingUpdateRepository.findByCollectionPointId(collectionPointId)
                        .orElseThrow(() -> new EntityNotFoundException("Nenhuma edição pendente para este ponto."));

                CollectionPoint point = pending.getCollectionPoint();
                point.setHasPendingUpdate(false);
                collectionPointRepository.save(point);
                pendingUpdateRepository.delete(pending);
        }

        private void applyUpdatesToEntity(CollectionPoint point, CollectionPointUpdateDTO dto) {
                collectionPointMapper.updateEntityFromDto(dto, point);

                if (dto.address() != null && point.getAddress() != null) {
                        collectionPointMapper.updateAddressFromDto(dto.address(), point.getAddress());
                }

                if (dto.clothTypeIds() != null && !dto.clothTypeIds().isEmpty()) {
                        List<ClothType> clothTypes = clothTypeRepository.findAllById(dto.clothTypeIds());
                        if (clothTypes.size() != dto.clothTypeIds().size()) {
                                throw new IllegalArgumentException("Um ou mais tipos de tecidos informados não foram encontrados.");
                        }
                        point.getClothTypes().clear();
                        point.getClothTypes().addAll(clothTypes);
                }

                if (dto.operatingHour() != null && !dto.operatingHour().isEmpty()) {
                        point.getOperatingHours().clear();
                        dto.operatingHour().stream()
                                .map(collectionPointMapper::toOperatingHourEntity)
                                .forEach(point.getOperatingHours()::add);
                }
        }

        @Transactional
        public CollectionPointUsersResponseDTO addOperatorToCollectionPoint(Long id, OperatorCreateRequestDTO operator, String managerEmail) {
                CollectionPoint collectionPoint = collectionPointRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Ponto de coleta não encontrado."));

                User manager = userRepository.findByEmail(managerEmail)
                        .orElseThrow(() -> new IllegalArgumentException("Gerente não encontrado."));

                if (!collectionPoint.getManagers().contains(manager)) {
                        throw new BadCredentialsException("Você não possui permissão para adicionar operadores a este ponto de coleta.");
                }

                String cleanDocument = operator.document().replaceAll("\\D", "");

                User user = userRepository.findByDocument(cleanDocument)
                        .orElseThrow(() -> new IllegalArgumentException("Operador não encontrado."));

                if(user.getRole().stream().noneMatch(role -> role.equals("ROLE_PONTO_COLETA_OPERADOR"))) {
                        user.getRole().add(Role.PONTO_COLETA_OPERADOR);
                }

                collectionPoint.getOperators().add(user);
                CollectionPoint saved = collectionPointRepository.save(collectionPoint);
                List<String> clothTypesNames = saved.getClothTypes().stream()
                        .map(ClothType::getName)
                        .collect(Collectors.toList());
                return new CollectionPointUsersResponseDTO(
                        new CollectionPointSummaryResponseDTO(saved, clothTypesNames),
                        saved.getManagers().stream().map(UserSummaryResponseDTO::new).collect(Collectors.toSet()),
                        saved.getOperators().stream().map(UserSummaryResponseDTO::new).collect(Collectors.toSet())
                );
        }

        @Transactional(readOnly = true)
        public CollectionPointUsersResponseDTO getOperatorsByCollectionPointId(Long id, String managerEmail) {
                CollectionPoint collectionPoint = collectionPointRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Ponto de coleta não encontrado."));

                collectionPoint.getManagers().stream()
                        .filter(manager -> manager.getEmail().equals(managerEmail))
                        .findFirst()
                        .orElseThrow(() -> new BadCredentialsException("Você não possui permissão para visualizar os operadores deste ponto de coleta."));

                Set<UserSummaryResponseDTO> managers = collectionPoint.getManagers().stream()
                        .map(UserSummaryResponseDTO::new)
                        .collect(Collectors.toSet());

                Set<UserSummaryResponseDTO> operators = collectionPoint.getOperators().stream()
                        .map(UserSummaryResponseDTO::new)
                        .collect(Collectors.toSet());

                List<String> clothTypesNames = collectionPoint.getClothTypes().stream()
                        .map(ClothType::getName)
                        .collect(Collectors.toList());

                CollectionPointSummaryResponseDTO collectionPointSummary = new CollectionPointSummaryResponseDTO(collectionPoint, clothTypesNames);

                return new CollectionPointUsersResponseDTO(collectionPointSummary, managers, operators);
        }

        @Transactional(readOnly = true)
        public Set<AssociationResponseDTO> getCollectionPointsByUserManager(User user) {
                Set<CollectionPoint> managedPoints = collectionPointRepository.findByManagersContainingAndStatusIn(user, Set.of(CollectionPointStatus.ACTIVE, CollectionPointStatus.PENDING));
                return managedPoints.stream()
                        .map(AssociationResponseDTO::new)
                        .collect(Collectors.toSet());
        }

        @Transactional(readOnly = true)
        public Set<AssociationResponseDTO> getCollectionPointsByUserOperator(User user) {
                Set<CollectionPoint> operatedPoints = collectionPointRepository.findByOperatorsContainingAndStatusIn(user, );
                return operatedPoints.stream()
                        .map(AssociationResponseDTO::new)
                        .collect(Collectors.toSet());
        }

        private CollectionPointResponseDTO mapToDTO(CollectionPoint entity) {
                var addr = entity.getAddress();

                return CollectionPointResponseDTO.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .status(entity.getStatus())
                        .hasPendingUpdate(entity.isHasPendingUpdate())
                        .pointPictureUrl(entity.getPointPictureUrl())
                        .address(CollectionPointResponseDTO.AddressResponseDTO.builder()
                                .street(addr.getStreet())
                                .number(addr.getNumber())
                                .complement(addr.getComplement())
                                .neighborhood(addr.getNeighborhood())
                                .city(addr.getCity())
                                .state(addr.getState().name())
                                .country(addr.getCountry())
                                .zipCode(addr.getZipCode())
                                .latitude(addr.getLatitude())
                                .longitude(addr.getLongitude())
                                .build())
                        .acceptedClothTypes(
                                entity.getClothTypes().stream()
                                        .map(ClothType::getName)
                                        .collect(Collectors.toSet()))
                        .operatingHours(
                                entity.getOperatingHours().stream()
                                        .map(h -> CollectionPointResponseDTO.OperatingHourResponseDTO
                                                .builder()
                                                .dayOfWeek(h.getDayOfWeek().name())
                                                .openingTime(h.getOpeningTime().toString())
                                                .closingTime(h.getClosingTime().toString())
                                                .build())
                                        .collect(Collectors.toSet()))
                        .build();
        }
}