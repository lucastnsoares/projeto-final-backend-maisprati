package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.CollectionPointCreateRequestDTO;
import br.com.maisprati.projeto.dto.request.CollectionPointUpdateDTO;
import br.com.maisprati.projeto.dto.request.OperatorCreateRequestDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointDistanceResponseDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointResponseDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointSummaryResponseDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointUsersResponseDTO;
import br.com.maisprati.projeto.dto.response.UserResponseDTO;
import br.com.maisprati.projeto.dto.response.UserSummaryResponseDTO;
import br.com.maisprati.projeto.mapper.CollectionPointMapper;
import br.com.maisprati.projeto.model.entity.Address;
import br.com.maisprati.projeto.model.entity.ClothType;
import br.com.maisprati.projeto.model.entity.CollectionPoint;
import br.com.maisprati.projeto.model.entity.OperatingHour;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.CollectionPointStatus;
import br.com.maisprati.projeto.model.enums.Role;
import br.com.maisprati.projeto.model.enums.State;
import br.com.maisprati.projeto.repository.ClothTypeRepository;
import br.com.maisprati.projeto.repository.CollectionPointRepository;
import br.com.maisprati.projeto.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionPointService {

        private static final BigDecimal MIN_LATITUDE = new BigDecimal("-90.0");
        private static final BigDecimal MAX_LATITUDE = new BigDecimal("90.0");
        private static final BigDecimal MIN_LONGITUDE = new BigDecimal("-180.0");
        private static final BigDecimal MAX_LONGITUDE = new BigDecimal("180.0");

        private final CollectionPointRepository collectionPointRepository;
        private final ClothTypeRepository clothTypeRepository;
        private final UserRepository userRepository;
        private final RegisterUserService registerUserService;
        private final UserValidationService userValidationService;

        private final CollectionPointMapper collectionPointMapper;

        @Transactional
        public CollectionPointResponseDTO createCollectionPoint(CollectionPointCreateRequestDTO dto,
                        String ownerEmail) {
                User owner = userRepository.findByEmail(ownerEmail)
                                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

                List<ClothType> clothTypes = clothTypeRepository.findAllById(dto.getClothTypeIds());
                if (clothTypes.isEmpty()) {
                        throw new IllegalArgumentException(
                                        "Informe ao menos um tipo de tecido válido cadastrado no sistema.");
                }

                if (clothTypes.size() != dto.getClothTypeIds().size()) {
                        throw new IllegalArgumentException(
                                        "Um ou mais tipos de tecidos informados não foram encontrados.");
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
                                .country(dto.getAddress().getCountry().trim().toLowerCase(Locale.ROOT))
                                .zipCode(dto.getAddress().getZipCode().trim())
                                .latitude(dto.getLatitude())
                                .longitude(dto.getLongitude())
                                .build();

                CollectionPoint collectionPoint = CollectionPoint.builder()
                                .name(dto.getName().trim().toUpperCase(Locale.ROOT))
                                .address(address)
                                .pointPictureUrl(dto.getImageUrl().trim())
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

                CollectionPoint saved = collectionPointRepository.save(collectionPoint);

                return mapToDTO(saved);
        }

        // Status do ponto de coleta
        @Transactional(readOnly = true)
        public Page<CollectionPointResponseDTO> findPendingCollectionPoints(Pageable pageable) {
                return collectionPointRepository.findByStatus(CollectionPointStatus.PENDING, pageable)
                        .map(this::mapToDTO);
        }

        @Transactional
        public CollectionPointResponseDTO approveCollectionPoint(Long id) {
                CollectionPoint point = collectionPointRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Ponto de coleta não encontrado com o ID: " + id));

                // Transição de Estado: PENDING -> ACTIVE
                point.setStatus(CollectionPointStatus.ACTIVE);

                CollectionPoint updatedPoint = collectionPointRepository.save(point);
                return mapToDTO(updatedPoint);
        }

        // Rejeição / Suspensão do ponto pelo Administrador
        @Transactional
        public CollectionPointResponseDTO rejectCollectionPoint(Long id) {
                CollectionPoint point = collectionPointRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Ponto de coleta não encontrado com o ID: " + id));

                // Transição de Estado: PENDING -> SUSPENDED
                point.setStatus(CollectionPointStatus.SUSPENDED);

                CollectionPoint updatedPoint = collectionPointRepository.save(point);
                return mapToDTO(updatedPoint);
        }

        // Consulta pública para o mapa - Retorna apenas pontos com status ACTIVE
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

        public Page<CollectionPointSummaryResponseDTO> findAll(Pageable pageable) {
                return collectionPointRepository.findAll(pageable)
                                .map(CollectionPointSummaryResponseDTO::new);
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
                                : new BigDecimal("15.0"); // Default 15km

                List<Long> effectiveClothTypeIds = (clothTypeIds != null && !clothTypeIds.isEmpty()) 
                        ? clothTypeIds 
                        : null;

                return collectionPointRepository.findNearby(userLat, userLng, effectiveRadius, effectiveClothTypeIds, pageable)
                                .map(CollectionPointDistanceResponseDTO::new);
        }

        @Transactional
        public CollectionPointResponseDTO updateCollectionPoint(Long id, CollectionPointUpdateDTO dto) {
        CollectionPoint collectionPoint = collectionPointRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ponto de coleta não encontrado."));

        collectionPointMapper.updateEntityFromDto(dto, collectionPoint);

         if (dto.address() != null && collectionPoint.getAddress() != null) {
                collectionPointMapper.updateAddressFromDto(dto.address(), collectionPoint.getAddress());
        }

        if (dto.clothTypeIds() != null && !dto.clothTypeIds().isEmpty()) {
                List<ClothType> clothTypes = clothTypeRepository.findAllById(dto.clothTypeIds());
                if (clothTypes.size() != dto.clothTypeIds().size()) {
                throw new IllegalArgumentException("Um ou mais tipos de tecidos informados não foram encontrados.");
                }
                collectionPoint.getClothTypes().clear();
                collectionPoint.getClothTypes().addAll(clothTypes);
        }

        if (dto.operatingHour() != null && !dto.operatingHour().isEmpty()) {
                collectionPoint.getOperatingHours().clear();
                dto.operatingHour().stream()
                        .map(collectionPointMapper::toOperatingHourEntity)
                        .forEach(collectionPoint.getOperatingHours()::add);
        }

        CollectionPoint saved = collectionPointRepository.save(collectionPoint);
        return mapToDTO(saved);
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

                User user = userRepository.findByEmail(operator.email())
                                .orElseThrow(() -> new IllegalArgumentException("Operador não encontrado."));

                if(user.getRole().stream().noneMatch(role -> role.equals("ROLE_PONTO_COLETA_OPERADOR"))) {
                        user.getRole().add(Role.PONTO_COLETA_OPERADOR);
                }

                collectionPoint.getOperators().add(user);
                CollectionPoint saved = collectionPointRepository.save(collectionPoint);
                return new CollectionPointUsersResponseDTO(
                                new CollectionPointSummaryResponseDTO(saved),
                                saved.getManagers().stream().map(UserSummaryResponseDTO::new).collect(Collectors.toSet()),
                                saved.getOperators().stream().map(UserSummaryResponseDTO::new).collect(Collectors.toSet())
                );
        }

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

                CollectionPointSummaryResponseDTO collectionPointSummary = new CollectionPointSummaryResponseDTO(collectionPoint);

                
                return new CollectionPointUsersResponseDTO(collectionPointSummary, managers, operators);
        }

        private CollectionPointResponseDTO mapToDTO(CollectionPoint entity) {
                var addr = entity.getAddress();

                return CollectionPointResponseDTO.builder()
                                .id(entity.getId())
                                .name(entity.getName())
                                .status(entity.getStatus())
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
                                                                                .openingTime(h.getOpeningTime()
                                                                                                .toString())
                                                                                .closingTime(h.getClosingTime()
                                                                                                .toString())
                                                                                .build())
                                                                .collect(Collectors.toSet()))
                                .build();
        }
}