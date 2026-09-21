package dev.iurygabriel.vagamatch.infrastructure.jobs.persistence;

import dev.iurygabriel.vagamatch.TestcontainersConfiguration;
import dev.iurygabriel.vagamatch.jobs.domain.Job;
import dev.iurygabriel.vagamatch.jobs.domain.JobRepository;
import dev.iurygabriel.vagamatch.jobs.domain.Seniority;
import dev.iurygabriel.vagamatch.jobs.domain.TechStack;
import dev.iurygabriel.vagamatch.jobs.domain.WorkModel;
import dev.iurygabriel.vagamatch.shared.application.port.EmbeddingProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
@DisplayName("JobRepositoryAdapter (integração com Postgres + pgvector)")
class JobRepositoryAdapterIT {

    @Autowired
    private JobRepository jobs;

    @Autowired
    private EmbeddingProvider embeddings;

    @Test
    @DisplayName("salva, recupera e conta uma vaga com embedding")
    void salva_recupera_e_conta_vaga_com_embedding() {
        Job job = Job.create(
                "greenhouse", "acme-1",
                "Frontend React Developer", "Acme",
                "Vaga de frontend com React e TypeScript, 100% remoto.",
                "https://acme.com/jobs/1",
                WorkModel.REMOTE, Seniority.MID, "Brasil",
                TechStack.of("react", "typescript", "node"),
                Instant.now());
        job.assignEmbedding(embeddings.embed(job.matchableText()));

        jobs.save(job);

        assertThat(jobs.existsBySourceAndExternalId("greenhouse", "acme-1")).isTrue();
        assertThat(jobs.count()).isEqualTo(1);

        Job found = jobs.findById(job.id()).orElseThrow();
        assertThat(found.title()).isEqualTo("Frontend React Developer");
        assertThat(found.companyName()).isEqualTo("Acme");
        assertThat(found.workModel()).isEqualTo(WorkModel.REMOTE);
        assertThat(found.stack().contains("react")).isTrue();
        assertThat(found.hasEmbedding()).isTrue();
        assertThat(found.embedding().dimensions()).isEqualTo(1536);
    }

    @Test
    @DisplayName("upsert por (source, external_id) não duplica")
    void upsert_nao_duplica() {
        Job first = Job.create("remotive", "r-99", "Node Dev", "Beta",
                "Backend Node", "https://x/1", WorkModel.REMOTE, Seniority.JUNIOR,
                "Brasil", TechStack.of("node"), Instant.now());
        jobs.save(first);

        Job again = Job.create("remotive", "r-99", "Node Dev (atualizado)", "Beta",
                "Backend Node e Nest", "https://x/1", WorkModel.REMOTE, Seniority.MID,
                "Brasil", TechStack.of("node", "nestjs"), Instant.now());
        jobs.save(again);

        assertThat(jobs.count()).isEqualTo(1);
    }
}
