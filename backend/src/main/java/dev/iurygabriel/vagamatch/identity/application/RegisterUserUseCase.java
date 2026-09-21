package dev.iurygabriel.vagamatch.identity.application;

import dev.iurygabriel.vagamatch.identity.application.port.PasswordHasher;
import dev.iurygabriel.vagamatch.identity.application.port.TokenService;
import dev.iurygabriel.vagamatch.identity.domain.Role;
import dev.iurygabriel.vagamatch.identity.domain.User;
import dev.iurygabriel.vagamatch.identity.domain.UserRepository;
import dev.iurygabriel.vagamatch.shared.domain.DomainException;
import org.springframework.stereotype.Service;

/** Caso de uso: registrar um novo usuário (papel USER) e já devolver o token. */
@Service
public class RegisterUserUseCase {

    private final UserRepository users;
    private final PasswordHasher hasher;
    private final TokenService tokens;

    public RegisterUserUseCase(UserRepository users, PasswordHasher hasher, TokenService tokens) {
        this.users = users;
        this.hasher = hasher;
        this.tokens = tokens;
    }

    public AuthResult register(String email, String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new DomainException("A senha deve ter ao menos 6 caracteres");
        }
        User candidate = User.create(email, hasher.hash(rawPassword), Role.USER);
        if (users.existsByEmail(candidate.email())) {
            throw new DomainException("E-mail já cadastrado");
        }
        User saved = users.save(candidate);
        return new AuthResult(tokens.issue(saved), saved.email(), saved.role());
    }
}
