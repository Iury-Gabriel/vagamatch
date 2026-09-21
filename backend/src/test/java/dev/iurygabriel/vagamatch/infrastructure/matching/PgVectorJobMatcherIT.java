package dev.iurygabriel.vagamatch.infrastructure.matching;

import dev.iurygabriel.vagamatch.TestcontainersConfiguration;
import dev.iurygabriel.vagamatch.jobs.domain.Job;
import dev.iurygabriel.vagamatch.jobs.domain.JobRepository;
import dev.iurygabriel.vagamatch.jobs.domain.Seniority;
import dev.iurygabriel.vagamatch.jobs.domain.TechStack;
import dev.iurygabriel.vagamatch.jobs.domain.WorkModel;
import dev.iurygabriel.vagamatch.matching.application.port.JobMatcher;
import dev.iurygabriel.vagamatch.matching.domain.JobMatch;
import dev.iurygabriel.vagamatch.matching.domain.MatchCriteria;
import dev.iurygabriel.vagamatch.shared.application.port.EmbeddingProvider;
import dev.iurygabriel.vagamatch.shared.domain.Embedding;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
@DisplayName("PgVectorJobMatcher (busca por similaridade no pgvector)")
class PgVectorJobMatcherIT {

    @Autowired
    private JobRepository jobs;

    @Autowired
    private JobMatcher matcher;

    @Autowired
    private EmbeddingProvider embeddings;

    @Test
    @DisplayName("ranqueia por similaridade: perfil idêntico à vaga a coloca em 1º com score ~1")
    void ranqueia_por_similaridade() {
        Job react = save("10", "React Developer", "React e TypeScript, remoto.",
                WorkModel.REMOTE, Seniority.MID, "react", "typescript");
        save("11", "Java Engineer", "Java e Spring Boot, backend.",
                WorkModel.REMOTE, Seniority.SENIOR, "java", "spring");

        Embedding profile = embeddings.embed(react.matchableText());
        List<JobMatch> result = matcher.findMatches(profile, MatchCriteria.topN(10));

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).job().externalId()).isEqualTo("10");
        assertThat(result.get(0).score()).isCloseTo(1.0, within(1e-4));
    }

    @Test
    @DisplayName("filtra por senioridade")
    void filtra_por_senioridade() {
        save("10", "React Developer", "React remoto", WorkModel.REMOTE, Seniority.MID, "react");
        Job senior = save("11", "Java Engineer", "Java remoto", WorkModel.REMOTE, Seniority.SENIOR, "java");

        List<JobMatch> result = matcher.findMatches(
                embeddings.embed("qualquer perfil"),
                new MatchCriteria(WorkModel.REMOTE, Set.of(Seniority.SENIOR), 10));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).job().externalId()).isEqualTo(senior.externalId());
    }

    private Job save(String externalId, String title, String description,
                     WorkModel workModel, Seniority seniority, String... stack) {
        Job job = Job.create("remotive", externalId, title, "Empresa", description,
                "https://x/" + externalId, workModel, seniority, "Brasil",
                TechStack.of(stack), Instant.now());
        job.assignEmbedding(embeddings.embed(job.matchableText()));
        return jobs.save(job);
    }
}
