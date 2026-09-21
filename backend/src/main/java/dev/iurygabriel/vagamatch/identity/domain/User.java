package dev.iurygabriel.vagamatch.identity.domain;

import dev.iurygabriel.vagamatch.shared.domain.DomainException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/** Aggregate root do contexto de identidade. Domínio puro. */
public class User {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final Instant createdAt;

    private User(UUID id, String email, String passwordHash, Role role, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.email = normalizeEmail(email);
        this.passwordHash = requireHash(passwordHash);
        this.role = Objects.requireNonNull(role);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static User create(String email, String passwordHash, Role role) {
        return new User(UUID.randomUUID(), email, passwordHash, role, Instant.now());
    }

    public static User rehydrate(UUID id, String email, String passwordHash, Role role, Instant createdAt) {
        return new User(id, email, passwordHash, role, createdAt);
    }

    private static String normalizeEmail(String email) {
        if (email == null || !EMAIL.matcher(email.trim()).matches()) {
            throw new DomainException("E-mail inválido");
        }
        return email.trim().toLowerCase();
    }

    private static String requireHash(String hash) {
        if (hash == null || hash.isBlank()) {
            throw new DomainException("Hash de senha obrigatório");
        }
        return hash;
    }

    public UUID id() { return id; }
    public String email() { return email; }
    public String passwordHash() { return passwordHash; }
    public Role role() { return role; }
    public Instant createdAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        return o instanceof User user && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
