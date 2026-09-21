package dev.iurygabriel.vagamatch.matching.application.port;

import dev.iurygabriel.vagamatch.matching.domain.JobMatch;
import dev.iurygabriel.vagamatch.matching.domain.MatchCriteria;
import dev.iurygabriel.vagamatch.shared.domain.Embedding;

import java.util.List;

/**
 * Porta de saída do matching: dado o embedding do perfil e os critérios,
 * retorna as vagas mais compatíveis, ranqueadas por similaridade.
 */
public interface JobMatcher {

    List<JobMatch> findMatches(Embedding profile, MatchCriteria criteria);
}
