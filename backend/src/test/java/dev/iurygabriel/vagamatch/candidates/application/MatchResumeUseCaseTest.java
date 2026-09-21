package dev.iurygabriel.vagamatch.candidates.application;

import dev.iurygabriel.vagamatch.candidates.application.port.ResumeTextExtractor;
import dev.iurygabriel.vagamatch.infrastructure.ai.MockEmbeddingProvider;
import dev.iurygabriel.vagamatch.jobs.domain.Job;
import dev.iurygabriel.vagamatch.jobs.domain.Seniority;
import dev.iurygabriel.vagamatch.jobs.domain.TechStack;
import dev.iurygabriel.vagamatch.jobs.domain.WorkModel;
import dev.iurygabriel.vagamatch.matching.application.FindMatchesUseCase;
import dev.iurygabriel.vagamatch.matching.application.port.JobMatcher;
import dev.iurygabriel.vagamatch.matching.domain.JobMatch;
import dev.iurygabriel.vagamatch.matching.domain.MatchCriteria;
import dev.iurygabriel.vagamatch.shared.domain.DomainException;
import dev.iurygabriel.vagamatch.shared.domain.Embedding;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatchResumeUseCaseTest {

    private final MockEmbeddingProvider embeddings = new MockEmbeddingProvider(1536);

    @Test
    @DisplayName("extrai o texto, gera embedding e retorna os matches")
    void retorna_matches_a_partir_do_pdf() {
        Job job = Job.create("remotive", "1", "React Dev", "Acme", "desc", "https://x/1",
                WorkModel.REMOTE, Seniority.MID, "Brasil", TechStack.of("react"), Instant.now());
        JobMatcher matcher = (profile, criteria) -> List.of(new JobMatch(job, 0.9));
        MatchResumeUseCase useCase = new MatchResumeUseCase(
                bytes -> "Currículo com React e Node", embeddings, new FindMatchesUseCase(matcher));

        List<JobMatch> result = useCase.match("pdf".getBytes(), MatchCriteria.topN(10));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).job().title()).isEqualTo("React Dev");
    }

    @Test
    @DisplayName("falha quando o currículo está vazio/ilegível")
    void falha_com_curriculo_vazio() {
        ResumeTextExtractor emptyExtractor = bytes -> "   ";
        JobMatcher matcher = (Embedding profile, MatchCriteria criteria) -> List.of();
        MatchResumeUseCase useCase = new MatchResumeUseCase(
                emptyExtractor, embeddings, new FindMatchesUseCase(matcher));

        assertThatThrownBy(() -> useCase.match("pdf".getBytes(), MatchCriteria.topN(10)))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("ilegível");
    }
}
