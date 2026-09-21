package dev.iurygabriel.vagamatch.jobs.application;

import dev.iurygabriel.vagamatch.jobs.application.port.JobSource;
import dev.iurygabriel.vagamatch.jobs.domain.Job;
import dev.iurygabriel.vagamatch.jobs.domain.JobRepository;
import dev.iurygabriel.vagamatch.shared.application.port.EmbeddingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso: ingerir vagas das fontes, gerar embedding e persistir.
 * Vagas já existentes (mesma source + external_id) são ignoradas.
 */
@Service
public class IngestJobsUseCase {

    private static final Logger log = LoggerFactory.getLogger(IngestJobsUseCase.class);

    private final List<JobSource> sources;
    private final JobRepository repository;
    private final EmbeddingProvider embeddings;

    public IngestJobsUseCase(List<JobSource> sources, JobRepository repository, EmbeddingProvider embeddings) {
        this.sources = sources;
        this.repository = repository;
        this.embeddings = embeddings;
    }

    public IngestionResult run() {
        int created = 0;
        int skipped = 0;
        for (JobSource source : sources) {
            List<Job> jobs = safeFetch(source);
            for (Job job : jobs) {
                if (repository.existsBySourceAndExternalId(job.source(), job.externalId())) {
                    skipped++;
                    continue;
                }
                job.assignEmbedding(embeddings.embed(job.matchableText()));
                repository.save(job);
                created++;
            }
        }
        log.info("Ingestão concluída: {} criadas, {} já existentes", created, skipped);
        return new IngestionResult(created, skipped);
    }

    private List<Job> safeFetch(JobSource source) {
        try {
            return source.fetch();
        } catch (RuntimeException ex) {
            log.warn("Falha ao buscar vagas da fonte '{}': {}", source.name(), ex.getMessage());
            return List.of();
        }
    }

    public record IngestionResult(int created, int skipped) {
        public int total() {
            return created + skipped;
        }
    }
}
