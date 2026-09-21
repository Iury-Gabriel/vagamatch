package dev.iurygabriel.vagamatch.jobs.application;

import dev.iurygabriel.vagamatch.jobs.application.IngestJobsUseCase.IngestionResult;
import dev.iurygabriel.vagamatch.jobs.application.port.JobSource;
import dev.iurygabriel.vagamatch.jobs.domain.Job;
import dev.iurygabriel.vagamatch.jobs.domain.JobRepository;
import dev.iurygabriel.vagamatch.jobs.domain.Seniority;
import dev.iurygabriel.vagamatch.jobs.domain.TechStack;
import dev.iurygabriel.vagamatch.jobs.domain.WorkModel;
import dev.iurygabriel.vagamatch.infrastructure.ai.MockEmbeddingProvider;
import dev.iurygabriel.vagamatch.shared.application.port.EmbeddingProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IngestJobsUseCaseTest {

    private final EmbeddingProvider embeddings = new MockEmbeddingProvider(1536);

    @Test
    @DisplayName("ingere vagas, gera embedding e persiste")
    void ingere_gera_embedding_e_persiste() {
        Job a = job("1", "React Dev", "react");
        Job b = job("2", "Node Dev", "node");
        InMemoryJobRepository repo = new InMemoryJobRepository();
        IngestJobsUseCase useCase = new IngestJobsUseCase(List.of(source("remotive", a, b)), repo, embeddings);

        IngestionResult result = useCase.run();

        assertThat(result.created()).isEqualTo(2);
        assertThat(result.skipped()).isZero();
        assertThat(repo.count()).isEqualTo(2);
        assertThat(repo.findById(a.id()).orElseThrow().hasEmbedding()).isTrue();
    }

    @Test
    @DisplayName("ignora vagas já existentes (mesma source + external_id)")
    void ignora_vagas_existentes() {
        Job a = job("1", "React Dev", "react");
        InMemoryJobRepository repo = new InMemoryJobRepository();
        repo.save(a);
        IngestJobsUseCase useCase = new IngestJobsUseCase(List.of(source("remotive", a)), repo, embeddings);

        IngestionResult result = useCase.run();

        assertThat(result.created()).isZero();
        assertThat(result.skipped()).isEqualTo(1);
    }

    @Test
    @DisplayName("uma fonte que falha não quebra a ingestão das demais")
    void fonte_com_falha_nao_quebra() {
        JobSource failing = new JobSource() {
            public String name() { return "bad"; }
            public List<Job> fetch() { throw new RuntimeException("api indisponível"); }
        };
        InMemoryJobRepository repo = new InMemoryJobRepository();
        IngestJobsUseCase useCase = new IngestJobsUseCase(
                List.of(failing, source("remotive", job("9", "Go Dev", "go"))), repo, embeddings);

        IngestionResult result = useCase.run();

        assertThat(result.created()).isEqualTo(1);
    }

    // --- helpers ---

    private static Job job(String externalId, String title, String tech) {
        return Job.create("remotive", externalId, title, "Acme", "descrição da vaga",
                "https://x/" + externalId, WorkModel.REMOTE, Seniority.MID, "Brasil",
                TechStack.of(tech), Instant.now());
    }

    private static JobSource source(String name, Job... jobs) {
        return new JobSource() {
            public String name() { return name; }
            public List<Job> fetch() { return List.of(jobs); }
        };
    }

    /** Repositório em memória para testar o caso de uso sem banco. */
    static class InMemoryJobRepository implements JobRepository {
        private final Map<UUID, Job> byId = new HashMap<>();
        private final Set<String> keys = new HashSet<>();

        public Job save(Job job) {
            byId.put(job.id(), job);
            keys.add(job.source() + "|" + job.externalId());
            return job;
        }

        public Optional<Job> findById(UUID id) {
            return Optional.ofNullable(byId.get(id));
        }

        public boolean existsBySourceAndExternalId(String source, String externalId) {
            return keys.contains(source + "|" + externalId);
        }

        public long count() {
            return byId.size();
        }

        public long countWithEmbedding() {
            return byId.values().stream().filter(Job::hasEmbedding).count();
        }
    }
}
