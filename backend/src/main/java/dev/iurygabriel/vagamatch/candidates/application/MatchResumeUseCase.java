package dev.iurygabriel.vagamatch.candidates.application;

import dev.iurygabriel.vagamatch.candidates.application.port.ResumeTextExtractor;
import dev.iurygabriel.vagamatch.matching.application.FindMatchesUseCase;
import dev.iurygabriel.vagamatch.matching.domain.JobMatch;
import dev.iurygabriel.vagamatch.matching.domain.MatchCriteria;
import dev.iurygabriel.vagamatch.shared.application.port.EmbeddingProvider;
import dev.iurygabriel.vagamatch.shared.domain.DomainException;
import dev.iurygabriel.vagamatch.shared.domain.Embedding;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso principal do produto: a partir do PDF do currículo,
 * extrai o texto, gera o embedding do perfil e retorna as vagas mais compatíveis.
 */
@Service
public class MatchResumeUseCase {

    private final ResumeTextExtractor extractor;
    private final EmbeddingProvider embeddings;
    private final FindMatchesUseCase findMatches;

    public MatchResumeUseCase(ResumeTextExtractor extractor, EmbeddingProvider embeddings,
                              FindMatchesUseCase findMatches) {
        this.extractor = extractor;
        this.embeddings = embeddings;
        this.findMatches = findMatches;
    }

    public List<JobMatch> match(byte[] pdfBytes, MatchCriteria criteria) {
        String resumeText = extractor.extractText(pdfBytes);
        if (resumeText == null || resumeText.isBlank()) {
            throw new DomainException("Currículo vazio ou ilegível");
        }
        Embedding profile = embeddings.embed(resumeText);
        return findMatches.execute(profile, criteria);
    }
}
