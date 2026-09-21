package dev.iurygabriel.vagamatch.interfaces.rest.dto;

import dev.iurygabriel.vagamatch.identity.application.AuthResult;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** DTOs de autenticação. */
public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank String password) {
    }

    public record LoginRequest(
            @NotBlank String email,
            @NotBlank String password) {
    }

    public record AuthResponse(String token, String email, String role) {
        public static AuthResponse from(AuthResult result) {
            return new AuthResponse(result.token(), result.email(), result.role().name());
        }
    }
}
