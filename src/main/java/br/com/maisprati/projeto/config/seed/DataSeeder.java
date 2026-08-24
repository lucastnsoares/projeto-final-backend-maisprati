package br.com.maisprati.projeto.config.seed;

import br.com.maisprati.projeto.model.entity.Usuario;
import br.com.maisprati.projeto.model.enums.Perfil;
import br.com.maisprati.projeto.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        List<Usuario> usuariosParaSalvar = new ArrayList<>();

        // Cria usuario admin
        if (!usuarioRepository.existsByEmail("admin1@email.com")) {
            Usuario admin = new Usuario();
            admin.setNome("Admin 1");
            admin.setDocumento("00000000001");
            admin.setEmail("admin1@email.com");
            admin.setSenha(passwordEncoder.encode("123456"));
            admin.setPerfil(Set.of(Perfil.ADMIN));
            admin.setAtivo(true);

            usuariosParaSalvar.add(admin);
        }

        // Cria gerente ponto coleta
        if (!usuarioRepository.existsByEmail("gerentepontocoleta1@email.com")) {
            Usuario gerente = new Usuario();
            gerente.setNome("Gerente Ponto Coleta 1");
            gerente.setDocumento("00000000002");
            gerente.setEmail("gerentepontocoleta1@email.com");
            gerente.setSenha(passwordEncoder.encode("123456"));
            gerente.setPerfil(Set.of(Perfil.GERENTE_PONTO_COLETA));
            gerente.setAtivo(true);

            usuariosParaSalvar.add(gerente);
        }

        // 3. Salva no banco apenas os que foram adicionados na lista
        if (!usuariosParaSalvar.isEmpty()) {
            usuarioRepository.saveAll(usuariosParaSalvar);
            System.out.println("************ Usuários de teste criados com sucesso! ************");
        }
    }
}
