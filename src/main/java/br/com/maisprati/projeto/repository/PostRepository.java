package br.com.maisprati.projeto.repository;

import br.com.maisprati.projeto.model.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsBySlug(String slug);
    Page<Post> findAllByPublishedTrue(Pageable pageable);
	Optional<Post> findByIdAndIsPublishedTrue(Long id);

    @Query("""
        SELECT p FROM Post p
        WHERE p.isPublished = true
          AND (:categoryId IS NULL OR p.category.id = :categoryId)
    """)
    Page<Post> findAllPublishedWithCategoryFilter(
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    @Query("""
        SELECT p FROM Post p
        WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
    """)
    Page<Post> findAllWithCategoryFilter(
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}
