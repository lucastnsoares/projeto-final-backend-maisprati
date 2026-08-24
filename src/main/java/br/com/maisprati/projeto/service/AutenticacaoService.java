package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Falha no login: O e-mail {} não existe no banco de dados.", email);
                    return new UsernameNotFoundException("Usuário não encontrado: " + email);
                });
    };
}
