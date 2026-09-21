package dev.iurygabriel.vagamatch.matching.domain;

import dev.iurygabriel.vagamatch.jobs.domain.Seniority;
import dev.iurygabriel.vagamatch.jobs.domain.WorkModel;

import java.util.Set;

/**
 * Filtros de negócio aplicados ao matching, além da similaridade semântica.
 * workModel/seniorities nulos ou vazios significam "qualquer".
 */
public record MatchCriteria(WorkModel workModel, Set<Seniority> seniorities, int limit) {

    public MatchCriteria {
        seniorities = seniorities == null ? Set.of() : Set.copyOf(seniorities);
        if (limit <= 0) {
            limit = 10;
        }
    }

    public static MatchCriteria topN(int n) {
        return new MatchCriteria(null, Set.of(), n);
    }

    public boolean hasWorkModel() {
        return workModel != null;
    }

    public boolean hasSeniorities() {
        return !seniorities.isEmpty();
    }
}
