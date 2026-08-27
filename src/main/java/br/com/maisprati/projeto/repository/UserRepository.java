package br.com.maisprati.projeto.repository;

import br.com.maisprati.projeto.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "role")
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    boolean existsByDocument(String document);

    @EntityGraph(attributePaths = "role")
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = "role")
    Page<User> findAll(Pageable pageable);
}
