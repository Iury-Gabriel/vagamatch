package dev.iurygabriel.vagamatch.matching.application;

import dev.iurygabriel.vagamatch.matching.application.port.JobMatcher;
import dev.iurygabriel.vagamatch.matching.domain.JobMatch;
import dev.iurygabriel.vagamatch.matching.domain.MatchCriteria;
import dev.iurygabriel.vagamatch.shared.domain.Embedding;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso: encontrar as vagas mais compatíveis com o perfil (embedding) da pessoa.
 * Ponto de extensão futuro: enriquecer cada match com uma explicação gerada por LLM.
 */
@Service
public class FindMatchesUseCase {

    private final JobMatcher matcher;

    public FindMatchesUseCase(JobMatcher matcher) {
        this.matcher = matcher;
    }

    public List<JobMatch> execute(Embedding profile, MatchCriteria criteria) {
        return matcher.findMatches(profile, criteria);
    }
}
