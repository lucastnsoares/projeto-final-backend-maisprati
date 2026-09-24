package br.com.maisprati.projeto.repository;

import br.com.maisprati.projeto.model.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsBySlug(String slug);
    Page<Post> findAllByPublishedTrue(Pageable pageable);
	Optional<Post> findByIdAndIsPublishedTrue(Long id);

    @Query(
        value = """
            SELECT DISTINCT p FROM Post p 
            LEFT JOIN p.tags t 
            WHERE p.isPublished = true 
              AND p.deletedAt IS NULL 
              AND (:categoryId IS NULL OR p.category.id = :categoryId) 
              AND (:searchTerm IS NULL OR lower(p.title) LIKE :searchTerm OR lower(p.summary) LIKE :searchTerm) 
              AND (:hasTags = false OR t IN :tags)
        """,
        countQuery = """
            SELECT COUNT(DISTINCT p) FROM Post p 
            LEFT JOIN p.tags t 
            WHERE p.isPublished = true 
              AND p.deletedAt IS NULL 
              AND (:categoryId IS NULL OR p.category.id = :categoryId) 
              AND (:searchTerm IS NULL OR lower(p.title) LIKE :searchTerm OR lower(p.summary) LIKE :searchTerm) 
              AND (:hasTags = false OR t IN :tags)
        """
    )
    Page<Post> findAllPublishedWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("searchTerm") String searchTerm,
            @Param("hasTags") boolean hasTags,
            @Param("tags") Set<String> tags,
            Pageable pageable
    );

    @Query(
        value = """
            SELECT DISTINCT p FROM Post p 
            LEFT JOIN p.tags t 
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId) 
              AND (:searchTerm IS NULL OR lower(p.title) LIKE :searchTerm OR lower(p.summary) LIKE :searchTerm) 
              AND (:hasTags = false OR t IN :tags)
        """,
        countQuery = """
            SELECT COUNT(DISTINCT p) FROM Post p 
            LEFT JOIN p.tags t 
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId) 
              AND (:searchTerm IS NULL OR lower(p.title) LIKE :searchTerm OR lower(p.summary) LIKE :searchTerm) 
              AND (:hasTags = false OR t IN :tags)
        """
    )
    Page<Post> findAllWithCategoryFilter(
            @Param("categoryId") Long categoryId,
            @Param("searchTerm") String searchTerm,
            @Param("hasTags") boolean hasTags,
            @Param("tags") Set<String> tags,
            Pageable pageable
    );
}
