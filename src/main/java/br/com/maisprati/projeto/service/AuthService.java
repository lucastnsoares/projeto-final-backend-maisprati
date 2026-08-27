package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Falha no login: O e-mail {} não existe no banco de dados.", email);
                    return new UsernameNotFoundException("Usuário não encontrado: " + email);
                });
        if(user.getPasswordHash() == null){
            throw new BadCredentialsException("Conta pendente de ativação. Defina a senha através do link enviado por e-mail.");
        }
        return user;
    }
}
