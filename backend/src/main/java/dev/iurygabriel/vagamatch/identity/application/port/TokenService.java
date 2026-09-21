package dev.iurygabriel.vagamatch.identity.application.port;

import dev.iurygabriel.vagamatch.identity.domain.Role;
import dev.iurygabriel.vagamatch.identity.domain.User;

import java.util.Optional;

/** Porta para emissão e validação de tokens JWT. */
public interface TokenService {

    String issue(User user);

    Optional<TokenPayload> validate(String token);

    record TokenPayload(String email, Role role) {
    }
}
