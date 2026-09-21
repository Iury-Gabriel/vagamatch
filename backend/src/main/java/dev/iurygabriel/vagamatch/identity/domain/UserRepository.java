package dev.iurygabriel.vagamatch.identity.domain;

import java.util.Optional;

/** Porta de saída do contexto de identidade. */
public interface UserRepository {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long count();
}
