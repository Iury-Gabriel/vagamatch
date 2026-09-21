package dev.iurygabriel.vagamatch.infrastructure.identity;

import dev.iurygabriel.vagamatch.identity.application.port.PasswordHasher;
import dev.iurygabriel.vagamatch.identity.domain.Role;
import dev.iurygabriel.vagamatch.identity.domain.User;
import dev.iurygabriel.vagamatch.identity.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Cria um usuário ADMIN padrão no primeiro start (dev), se ainda não existir. */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    private static final String ADMIN_EMAIL = "admin@vagamatch.dev";
    private static final String ADMIN_PASSWORD = "admin123";

    private final UserRepository users;
    private final PasswordHasher hasher;

    public AdminSeeder(UserRepository users, PasswordHasher hasher) {
        this.users = users;
        this.hasher = hasher;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!users.existsByEmail(ADMIN_EMAIL)) {
            users.save(User.create(ADMIN_EMAIL, hasher.hash(ADMIN_PASSWORD), Role.ADMIN));
            log.info("Usuário ADMIN padrão criado: {} (senha dev: {})", ADMIN_EMAIL, ADMIN_PASSWORD);
        }
    }
}
