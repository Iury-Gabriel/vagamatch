package dev.iurygabriel.vagamatch.identity.application;

import dev.iurygabriel.vagamatch.identity.application.port.PasswordHasher;
import dev.iurygabriel.vagamatch.identity.application.port.TokenService;
import dev.iurygabriel.vagamatch.identity.domain.User;
import dev.iurygabriel.vagamatch.identity.domain.UserRepository;
import dev.iurygabriel.vagamatch.shared.domain.DomainException;
import org.springframework.stereotype.Service;

/** Caso de uso: autenticar por e-mail/senha e emitir o token JWT. */
@Service
public class AuthenticateUseCase {

    private final UserRepository users;
    private final PasswordHasher hasher;
    private final TokenService tokens;

    public AuthenticateUseCase(UserRepository users, PasswordHasher hasher, TokenService tokens) {
        this.users = users;
        this.hasher = hasher;
        this.tokens = tokens;
    }

    public AuthResult login(String email, String rawPassword) {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        User user = users.findByEmail(normalized)
                .orElseThrow(() -> new DomainException("Credenciais inválidas"));
        if (!hasher.matches(rawPassword, user.passwordHash())) {
            throw new DomainException("Credenciais inválidas");
        }
        return new AuthResult(tokens.issue(user), user.email(), user.role());
    }
}
