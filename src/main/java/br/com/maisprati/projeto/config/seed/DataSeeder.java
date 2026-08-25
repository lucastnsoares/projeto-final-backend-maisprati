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
    public void run(String... args){

        System.out.println("Timezone: " + java.time.ZoneId.systemDefault());
        System.out.println("Data/hora: " + java.time.LocalDateTime.now());
        List<User> usersToSave = new ArrayList<>();

        // Cria usuário admin
        if (!userRepository.existsByEmail("admin1@email.com")) {
            User admin = new User();
            admin.setName("Admin 1");
            admin.setDocument("00000000001");
            admin.setEmail("admin1@email.com");
            admin.setPasswordHash(passwordEncoder.encode("123456"));
            admin.setRole(Set.of(Role.ADMIN));
            admin.setActive(true);

            usersToSave.add(admin);
        }

        // Cria usuário doador
        if (!userRepository.existsByEmail("usuariodoador1@email.com")) {
            User user = new User();
            user.setName("Usuario Doador 1");
            user.setDocument("00000000002");
            user.setEmail("usuariodoador1@email.com");
            user.setPasswordHash(passwordEncoder.encode("123456"));
            user.setRole(Set.of(Role.DOADOR));
            user.setActive(true);

            usersToSave.add(user);
        }

        // 3. Salva no banco apenas os que foram adicionados na lista
        if (!usersToSave.isEmpty()) {
            userRepository.saveAll(usersToSave);
            System.out.println("************ Usuários de teste criados com sucesso! ************");
        }
    }
}
