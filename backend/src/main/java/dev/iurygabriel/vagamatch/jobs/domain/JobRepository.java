package dev.iurygabriel.vagamatch.jobs.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saída (repository) do contexto de Vagas.
 * O domínio define o contrato; a infraestrutura fornece o adapter (JPA/pgvector).
 */
public interface JobRepository {

    Job save(Job job);

    Optional<Job> findById(UUID id);

    boolean existsBySourceAndExternalId(String source, String externalId);

    long count();

    /** Quantas vagas já possuem embedding (prontas para matching). */
    long countWithEmbedding();
}
