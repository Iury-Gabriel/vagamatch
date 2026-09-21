package dev.iurygabriel.vagamatch.identity.application;

import dev.iurygabriel.vagamatch.identity.domain.Role;

/** Resultado de autenticação: token JWT + dados básicos do usuário. */
public record AuthResult(String token, String email, Role role) {
}
