package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.CollectionPointCreateRequestDTO;
import br.com.maisprati.projeto.dto.response.CollectionPointResponseDTO;
import br.com.maisprati.projeto.model.entity.Address;
import br.com.maisprati.projeto.model.entity.ClothType;
import br.com.maisprati.projeto.model.entity.CollectionPoint;
import br.com.maisprati.projeto.model.entity.OperatingHour;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.CollectionPointStatus;
import br.com.maisprati.projeto.model.enums.State;
import br.com.maisprati.projeto.repository.ClothTypeRepository;
import br.com.maisprati.projeto.repository.CollectionPointRepository;
import br.com.maisprati.projeto.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionPointService {

    private final CollectionPointRepository collectionPointRepository;
    private final ClothTypeRepository clothTypeRepository;
    private final UserRepository userRepository;

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
                .complement(dto.getAddress().getComplement().trim().toUpperCase(Locale.ROOT))
                .neighborhood(dto.getAddress().getNeighborhood().trim().toUpperCase(Locale.ROOT))
                .city(dto.getAddress().getCity().trim().toUpperCase(Locale.ROOT))
                .state(State.valueOf(dto.getAddress().getState().trim().toUpperCase()))
                .country(dto.getAddress().getCountry().trim().toLowerCase(Locale.ROOT))
                .zipCode(dto.getAddress().getZipCode().trim())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        CollectionPoint collectionPoint = CollectionPoint.builder()
                .name(dto.getName().trim())
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
                                .collect(Collectors.toSet())
                )
                .operatingHours(
                        entity.getOperatingHours().stream()
                                .map(h -> CollectionPointResponseDTO.OperatingHourResponseDTO.builder()
                                        .dayOfWeek(h.getDayOfWeek().name())
                                        .openingTime(h.getOpeningTime().toString())
                                        .closingTime(h.getClosingTime().toString())
                                        .build())
                                .collect(Collectors.toSet())
                )
                .build();
    }
}