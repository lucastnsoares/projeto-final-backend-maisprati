package br.com.maisprati.projeto.repository;

import br.com.maisprati.projeto.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsBySlug(String slug);
}
