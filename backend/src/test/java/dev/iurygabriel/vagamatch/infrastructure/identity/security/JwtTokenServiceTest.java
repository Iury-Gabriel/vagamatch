package dev.iurygabriel.vagamatch.infrastructure.identity.security;

import dev.iurygabriel.vagamatch.identity.application.port.TokenService.TokenPayload;
import dev.iurygabriel.vagamatch.identity.domain.Role;
import dev.iurygabriel.vagamatch.identity.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceTest {

    private final JwtTokenService service = new JwtTokenService(
            new JwtProperties("Y2hhbmdlLW1lLXRoaXMtaXMtYS1kZXYtb25seS1zZWNyZXQtMzIrYnl0ZXM=", 120));

    @Test
    @DisplayName("emite e valida um token, preservando email e papel")
    void emite_e_valida() {
        User user = User.create("iury@vagamatch.dev", "hash", Role.ADMIN);

        String token = service.issue(user);
        TokenPayload payload = service.validate(token).orElseThrow();

        assertThat(payload.email()).isEqualTo("iury@vagamatch.dev");
        assertThat(payload.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("token inválido retorna vazio")
    void token_invalido() {
        assertThat(service.validate("nao-e-um-token")).isEmpty();
    }
}
