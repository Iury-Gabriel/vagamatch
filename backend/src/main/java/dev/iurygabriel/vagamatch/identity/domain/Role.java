package dev.iurygabriel.vagamatch.identity.domain;

/** Papel do usuário na plataforma. */
public enum Role {
    USER,
    ADMIN;

    /** Authority no formato esperado pelo Spring Security (ROLE_*). */
    public String authority() {
        return "ROLE_" + name();
    }
}
