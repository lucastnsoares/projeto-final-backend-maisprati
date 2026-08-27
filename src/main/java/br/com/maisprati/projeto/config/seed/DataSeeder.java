package br.com.maisprati.projeto.config.seed;

import br.com.maisprati.projeto.model.entity.User;
import br.com.maisprati.projeto.model.enums.Role;
import br.com.maisprati.projeto.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<User> usersToSave = new ArrayList<>();

        // 1. Cria usuário Administrador
        if (!userRepository.existsByEmail("admin@projeto.com.br")) {
            User admin = new User();
            admin.setName("ADMINISTRADOR DO SISTEMA");
            admin.setDocument("52998224725"); // CPF válido
            admin.setEmail("admin@projeto.com.br");
            admin.setPhone("+5531987654321");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123456"));
            admin.setRole(Set.of(Role.ADMIN));
            admin.setActive(true);
            usersToSave.add(admin);
        }

        // 2. Cria usuário Doador (usuário padrão)
        if (!userRepository.existsByEmail("doador@projeto.com.br")) {
            User doador = new User();
            doador.setName("USUARIO DOADOR TESTE");
            doador.setDocument("11144477735");
            doador.setEmail("doador@projeto.com.br");
            doador.setPhone("+5511912345678");
            doador.setPasswordHash(passwordEncoder.encode("Doador@123456"));
            doador.setRole(Set.of(Role.DOADOR));
            doador.setActive(true);
            usersToSave.add(doador);
        }

        // 3. Salva no banco
        if (!usersToSave.isEmpty()) {
            userRepository.saveAll(usersToSave);
            System.out.println("************ Usuários de teste criados com sucesso! ************");
        }
    }
}