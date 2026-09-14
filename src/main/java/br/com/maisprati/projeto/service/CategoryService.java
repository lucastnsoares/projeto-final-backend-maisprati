package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.CategoryRequestDTO;
import br.com.maisprati.projeto.dto.response.CategoryResponseDTO;
import br.com.maisprati.projeto.model.entity.Category;
import br.com.maisprati.projeto.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;


    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        if (categoryRepository.existsByName(dto.name())) {
            throw new IllegalArgumentException("Categoria já existente");
        }
        Category category = new Category();
        category.setName(dto.name());
        categoryRepository.save(category);
        return new CategoryResponseDTO(category);
    }

    public Page<CategoryResponseDTO> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(CategoryResponseDTO::new);
    }

    public CategoryResponseDTO findCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria inexistente"));
        return new CategoryResponseDTO(category);
    }
}
