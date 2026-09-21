package dev.iurygabriel.vagamatch.shared.domain;

import java.util.Arrays;

/**
 * Value Object que representa um vetor de embedding.
 * Encapsula a dimensão e a regra de similaridade de cosseno (domínio puro).
 */
public record Embedding(float[] vector) {

    public Embedding {
        if (vector == null || vector.length == 0) {
            throw new DomainException("Embedding não pode ser vazio");
        }
    }

    public int dimensions() {
        return vector.length;
    }

    /** Similaridade de cosseno em [-1, 1]. Usada como base do score de match. */
    public double cosineSimilarity(Embedding other) {
        if (other == null || other.vector.length != this.vector.length) {
            throw new DomainException("Dimensões de embedding incompatíveis");
        }
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < vector.length; i++) {
            dot += vector[i] * other.vector[i];
            normA += vector[i] * vector[i];
            normB += other.vector[i] * other.vector[i];
        }
        if (normA == 0 || normB == 0) {
            return 0d;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Embedding other && Arrays.equals(vector, other.vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }

    @Override
    public String toString() {
        return "Embedding[dim=" + vector.length + "]";
    }
}
