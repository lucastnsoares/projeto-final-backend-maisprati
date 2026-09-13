package br.com.maisprati.projeto.repository;

import br.com.maisprati.projeto.model.entity.PasswordResetToken;
import br.com.maisprati.projeto.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    Boolean existsByTokenHash(String tokenHash);
    void deleteByUser(User user);
}
