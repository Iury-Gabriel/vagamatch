package dev.iurygabriel.vagamatch.identity.application.port;

/** Porta para hashing de senha (implementada com BCrypt na infraestrutura). */
public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hash);
}
