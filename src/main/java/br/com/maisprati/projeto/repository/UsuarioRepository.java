package br.com.maisprati.projeto.repository;

import br.com.maisprati.projeto.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<Usuario> findByDocumento(String documento);
    boolean existsByDocumento(String documento);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.enderecos WHERE u.email = :email")
    Optional<Usuario> findByEmailComEnderecos(String email);
}
