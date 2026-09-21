package dev.iurygabriel.vagamatch.matching.domain;

import dev.iurygabriel.vagamatch.jobs.domain.Job;

/**
 * Resultado do matching: uma vaga e seu score de compatibilidade [0, 1]
 * (similaridade de cosseno entre o perfil e a vaga).
 */
public record JobMatch(Job job, double score) {

    /** Score em porcentagem (0–100), para exibição. */
    public int scorePercent() {
        return (int) Math.round(Math.max(0, Math.min(1, score)) * 100);
    }
}
