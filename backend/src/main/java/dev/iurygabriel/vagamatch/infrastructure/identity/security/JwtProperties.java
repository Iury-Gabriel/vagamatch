package dev.iurygabriel.vagamatch.infrastructure.identity.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuração do JWT (vagamatch.security.jwt.*). */
@ConfigurationProperties("vagamatch.security.jwt")
public record JwtProperties(String secret, long expirationMinutes) {

    public JwtProperties {
        if (expirationMinutes <= 0) {
            expirationMinutes = 120;
        }
    }
}
