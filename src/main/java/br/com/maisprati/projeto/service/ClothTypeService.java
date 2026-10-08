package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.ClothTypeRequestDTO;
import br.com.maisprati.projeto.dto.request.ClothTypeUpdateRequestDTO;
import br.com.maisprati.projeto.dto.response.ClothTypeResponseDTO;
import br.com.maisprati.projeto.model.entity.ClothType;
import br.com.maisprati.projeto.repository.ClothTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClothTypeService {
    private final ClothTypeRepository clothTypeRepository;

    @Transactional(readOnly = true)
    public Page<ClothTypeResponseDTO> findAll(Pageable pageable) {
        return clothTypeRepository.findAll(pageable)
                .map(ClothTypeResponseDTO::new);
    }

    @Transactional
    public ClothTypeResponseDTO createClothType(ClothTypeRequestDTO dto) {
        if (dto.name() == null || dto.name().isEmpty()) {
            throw new IllegalArgumentException("O nome do tecido deve ser preenchido");
        }

        if (clothTypeRepository.existsByName(dto.name())) {
            throw new IllegalArgumentException("O tipo de tecido já existe no banco de dados");
        }

        ClothType newClothType = new ClothType();
        newClothType.setName(dto.name().trim());
        newClothType.setDescription(dto.description().trim());
        clothTypeRepository.save(newClothType);

        return new ClothTypeResponseDTO(newClothType);
    }

    @Transactional(readOnly = true)
    public ClothTypeResponseDTO findById(Long id) {
        ClothType clothType = clothTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de tecido não localizado"));
        return new ClothTypeResponseDTO(clothType);
    }

    @Transactional
    public ClothTypeResponseDTO updateClothType(Long id, ClothTypeUpdateRequestDTO dto) {
        ClothType clothType = clothTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de tecido não localizado."));

        if (dto.name() != null && !dto.name().isBlank()) {
            if (clothTypeRepository.existsByNameIgnoreCaseAndIdNot(dto.name(), clothType.getId())) {
                throw new IllegalArgumentException("Tecido já existente.");
            }
            clothType.setName(dto.name().trim());
        }
        if (dto.description() != null && !dto.description().isBlank()) {
            clothType.setDescription(dto.description().trim());
        }
        if (dto.isActive() != null) {
            clothType.setActive(dto.isActive());
        }
        clothTypeRepository.save(clothType);
        return new ClothTypeResponseDTO(clothType);
    }
}
