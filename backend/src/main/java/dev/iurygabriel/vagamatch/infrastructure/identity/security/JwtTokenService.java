package dev.iurygabriel.vagamatch.infrastructure.identity.security;

import dev.iurygabriel.vagamatch.identity.application.port.TokenService;
import dev.iurygabriel.vagamatch.identity.domain.Role;
import dev.iurygabriel.vagamatch.identity.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/** Emissão e validação de JWT (HS256) com jjwt. */
@Component
public class JwtTokenService implements TokenService {

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtTokenService(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
        this.expirationMinutes = properties.expirationMinutes();
    }

    @Override
    public String issue(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.email())
                .claim("role", user.role().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
                .signWith(key)
                .compact();
    }

    @Override
    public Optional<TokenPayload> validate(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Role role = Role.valueOf(claims.get("role", String.class));
            return Optional.of(new TokenPayload(claims.getSubject(), role));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }
}
