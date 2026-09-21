package dev.iurygabriel.vagamatch.infrastructure.identity.persistence;

import dev.iurygabriel.vagamatch.identity.domain.Role;
import dev.iurygabriel.vagamatch.identity.domain.User;
import dev.iurygabriel.vagamatch.identity.domain.UserRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private static final String SELECT =
            "SELECT id, email, password_hash, role, created_at FROM users WHERE email = :email";

    private final JdbcClient jdbc;

    public UserRepositoryAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public User save(User user) {
        jdbc.sql("""
                        INSERT INTO users (id, email, password_hash, role, created_at)
                        VALUES (:id, :email, :passwordHash, :role, :createdAt)
                        """)
                .param("id", user.id())
                .param("email", user.email())
                .param("passwordHash", user.passwordHash())
                .param("role", user.role().name())
                .param("createdAt", user.createdAt().atOffset(ZoneOffset.UTC))
                .update();
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jdbc.sql(SELECT)
                .param("email", email)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public boolean existsByEmail(String email) {
        return Boolean.TRUE.equals(jdbc.sql("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
                .param("email", email)
                .query(Boolean.class)
                .single());
    }

    @Override
    public long count() {
        return jdbc.sql("SELECT count(*) FROM users").query(Long.class).single();
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.rehydrate(
                rs.getObject("id", UUID.class),
                rs.getString("email"),
                rs.getString("password_hash"),
                Role.valueOf(rs.getString("role")),
                rs.getTimestamp("created_at").toInstant());
    }
}
