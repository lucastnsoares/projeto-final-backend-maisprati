package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.PostCreateRequestDTO;
import br.com.maisprati.projeto.dto.request.PostEditRequestDTO;
import br.com.maisprati.projeto.dto.response.PostResponseDTO;
import br.com.maisprati.projeto.dto.response.PostSummaryResponseDTO;
import br.com.maisprati.projeto.model.entity.Category;
import br.com.maisprati.projeto.model.entity.ClothType;
import br.com.maisprati.projeto.model.entity.Post;
import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.repository.CategoryRepository;
import br.com.maisprati.projeto.repository.ClothTypeRepository;
import br.com.maisprati.projeto.repository.PostRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final ClothTypeRepository clothTypeRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Transactional
    public PostResponseDTO createPost(PostCreateRequestDTO dto, User loggedInUser) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria informada não foi encontrada."));

        validateAccessibilityAndCoverImage(dto.coverImageUrl(), dto.coverImageAlt());

        String slug = generateUniqueSlug(dto.title());

        Set<ClothType> clothTypes = resolveClothTypes(dto.clothTypeIds());

        Set<String> sanitizedTags = sanitizeTags(dto.tags());

        Post post = new Post();
        post.setTitle(dto.title().trim());
        post.setSlug(slug);
        post.setSummary(dto.summary().trim());
        post.setBody(dto.body().trim());
        post.setCategory(category);
        post.setCreatedBy(loggedInUser);
        post.setCoverImageUrl(dto.coverImageUrl() != null ? dto.coverImageUrl().trim() : null);
        post.setCoverImageAlt(dto.coverImageAlt() != null ? dto.coverImageAlt().trim() : null);
        post.setTags(sanitizedTags);
        post.setClothTypes(clothTypes);
        post.setViewCount(0L);
        post.setPublished(true);

        Post savedPost = postRepository.save(post);
        return new PostResponseDTO(savedPost);
    }

    @Transactional(readOnly = true)
    public Page<PostSummaryResponseDTO> findAllByPublishedTrue(Long categoryId, Pageable pageable) {
        return postRepository.findAllPublishedWithCategoryFilter(categoryId, pageable)
                .map(PostSummaryResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public PostResponseDTO findPostByIdAndIsPublishedTrue(Long id) {
        Post post = postRepository.findByIdAndIsPublishedTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Post inexistente ou não disponível."));
        return new PostResponseDTO(post);
    }

    //Apenas admin
    @Transactional(readOnly = true)
    public Page<PostSummaryResponseDTO> findAllPosts(Long categoryId, Pageable pageable) {
        return postRepository.findAllWithCategoryFilter(categoryId, pageable)
                .map(PostSummaryResponseDTO::new);
    }

    //Apenas admin
    @Transactional
    public PostResponseDTO updatePost(Long id, PostEditRequestDTO dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post inexistente."));

        String targetImageUrl = dto.coverImageUrl() != null ? dto.coverImageUrl().trim() : post.getCoverImageUrl();
        String targetImageAlt = dto.coverImageAlt() != null ? dto.coverImageAlt().trim() : post.getCoverImageAlt();
        validateAccessibilityAndCoverImage(targetImageUrl, targetImageAlt);
        
        if (dto.title() != null && !dto.title().isBlank()) {
            post.setTitle(dto.title().trim());
            String newSlug = generateUniqueSlug(dto.title());
            post.setSlug(newSlug);
        }
        if (dto.summary() != null && !dto.summary().isBlank()) {
            post.setSummary(dto.summary().trim());
        }
        if (dto.body() != null && !dto.body().isBlank()) {
            post.setBody(dto.body().trim());
        }
        if (dto.coverImageUrl() != null && !dto.coverImageUrl().isBlank()) {
            post.setCoverImageUrl(dto.coverImageUrl().trim());
        }
        if (dto.coverImageAlt() != null && !dto.coverImageAlt().isBlank()) {
            post.setCoverImageAlt(dto.coverImageAlt().trim());
        }
        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoria informada não foi encontrada."));
            post.setCategory(category);
        }
        if (dto.clothTypeIds() != null) {
            Set<ClothType> clothTypes = resolveClothTypes(dto.clothTypeIds());
            post.setClothTypes(clothTypes);
        }
        if (dto.tags() != null) {
            Set<String> sanitizedTags = sanitizeTags(dto.tags());
            post.setTags(sanitizedTags);
        }
        if (dto.isPublished() != null) {
            post.setPublished(dto.isPublished());
        }
        Post updatedPost = postRepository.save(post);
        return new PostResponseDTO(updatedPost);
    }

    //apenas ADMIN
    @Transactional(readOnly = true)
    public PostResponseDTO findPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post inexistente ou não disponível."));
        return new PostResponseDTO(post);
    }


    private void validateAccessibilityAndCoverImage(String coverImageUrl, String coverImageAlt) {
        boolean hasImageUrl = coverImageUrl != null && !coverImageUrl.isBlank();
        boolean hasImageAlt = coverImageAlt != null && !coverImageAlt.isBlank();

        if (hasImageUrl && !hasImageAlt) {
            throw new IllegalArgumentException("O texto alternativo (coverImageAlt) é obrigatório quando uma imagem de capa é fornecida.");
        }
    }

    private Set<ClothType> resolveClothTypes(Set<Long> clothTypeIds) {
        if (clothTypeIds == null || clothTypeIds.isEmpty()) {
            return new HashSet<>();
        }

        List<ClothType> foundTypes = clothTypeRepository.findAllById(clothTypeIds);
        if (foundTypes.size() != clothTypeIds.size()) {
            Set<Long> foundIds = foundTypes.stream()
                    .map(ClothType::getId)
                    .collect(Collectors.toSet());

            Set<Long> missingIds = clothTypeIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());

            throw new IllegalArgumentException("Os seguintes IDs de tipos de tecido não foram encontrados: " + missingIds);
        }

        return new HashSet<>(foundTypes);
    }

    private Set<String> sanitizeTags(Set<String> rawTags) {
        if (rawTags == null || rawTags.isEmpty()) {
            return new HashSet<>();
        }

        return rawTags.stream()
                .filter(tag -> tag != null && !tag.isBlank())
                .map(tag -> tag.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    private String generateUniqueSlug(String title) {
        String baseSlug = toSlug(title);
        String slug = baseSlug;
        int sequence = 1;

        while (postRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + sequence;
            sequence++;
        }

        return slug;
    }

    private String toSlug(String input) {
        if (input == null) {
            return "";
        }
        String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH).replaceAll("-{2,}", "-").replaceAll("^-|-$", "");
    }

}
