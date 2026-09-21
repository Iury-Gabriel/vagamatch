package dev.iurygabriel.vagamatch.identity.application;

import dev.iurygabriel.vagamatch.identity.application.port.PasswordHasher;
import dev.iurygabriel.vagamatch.identity.application.port.TokenService;
import dev.iurygabriel.vagamatch.identity.domain.Role;
import dev.iurygabriel.vagamatch.identity.domain.User;
import dev.iurygabriel.vagamatch.identity.domain.UserRepository;
import dev.iurygabriel.vagamatch.shared.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentityUseCasesTest {

    private final InMemoryUserRepository users = new InMemoryUserRepository();
    private final PasswordHasher hasher = new FakeHasher();
    private final TokenService tokens = new FakeTokens();

    @Nested
    class Registro {
        private final RegisterUserUseCase register = new RegisterUserUseCase(users, hasher, tokens);

        @Test
        @DisplayName("registra e devolve token com papel USER")
        void registra() {
            AuthResult result = register.register("iury@x.com", "secret123");
            assertThat(result.email()).isEqualTo("iury@x.com");
            assertThat(result.role()).isEqualTo(Role.USER);
            assertThat(result.token()).isNotBlank();
            assertThat(users.existsByEmail("iury@x.com")).isTrue();
        }

        @Test
        @DisplayName("falha com e-mail duplicado")
        void email_duplicado() {
            users.save(User.create("iury@x.com", hasher.hash("secret123"), Role.USER));
            assertThatThrownBy(() -> register.register("iury@x.com", "secret123"))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("já cadastrado");
        }

        @Test
        @DisplayName("falha com senha curta")
        void senha_curta() {
            assertThatThrownBy(() -> register.register("a@b.com", "123"))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("6 caracteres");
        }
    }

    @Nested
    class Login {
        private final AuthenticateUseCase authenticate = new AuthenticateUseCase(users, hasher, tokens);

        @Test
        @DisplayName("autentica com credenciais corretas")
        void login_ok() {
            users.save(User.create("iury@x.com", hasher.hash("secret123"), Role.USER));
            AuthResult result = authenticate.login("iury@x.com", "secret123");
            assertThat(result.token()).isNotBlank();
        }

        @Test
        @DisplayName("falha com senha incorreta")
        void senha_incorreta() {
            users.save(User.create("iury@x.com", hasher.hash("secret123"), Role.USER));
            assertThatThrownBy(() -> authenticate.login("iury@x.com", "errada"))
                    .hasMessageContaining("Credenciais inválidas");
        }

        @Test
        @DisplayName("falha com usuário inexistente")
        void inexistente() {
            assertThatThrownBy(() -> authenticate.login("nao@existe.com", "x"))
                    .hasMessageContaining("Credenciais inválidas");
        }
    }

    // --- fakes ---

    static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> byEmail = new HashMap<>();

        public User save(User user) {
            byEmail.put(user.email(), user);
            return user;
        }

        public Optional<User> findByEmail(String email) {
            return Optional.ofNullable(byEmail.get(email));
        }

        public boolean existsByEmail(String email) {
            return byEmail.containsKey(email);
        }

        public long count() {
            return byEmail.size();
        }
    }

    static class FakeHasher implements PasswordHasher {
        public String hash(String rawPassword) {
            return "hashed:" + rawPassword;
        }

        public boolean matches(String rawPassword, String hash) {
            return hash.equals("hashed:" + rawPassword);
        }
    }

    static class FakeTokens implements TokenService {
        public String issue(User user) {
            return "token-" + user.email();
        }

        public Optional<TokenPayload> validate(String token) {
            return Optional.empty();
        }
    }
}
