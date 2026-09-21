package dev.iurygabriel.vagamatch.infrastructure.ai;

import dev.iurygabriel.vagamatch.shared.application.port.EmbeddingProvider;
import dev.iurygabriel.vagamatch.shared.domain.Embedding;

import java.util.Random;

/**
 * Provider de embeddings determinístico, sem custo, para desenvolvimento e testes.
 * Gera um vetor estável a partir do texto (mesmo texto → mesmo vetor), normalizado (L2).
 * Ativado por padrão (vagamatch.ai.provider=mock).
 */
public class MockEmbeddingProvider implements EmbeddingProvider {

    private final int dimensions;

    public MockEmbeddingProvider(int dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public Embedding embed(String text) {
        String normalized = text == null ? "" : text.trim().toLowerCase();
        Random random = new Random(normalized.hashCode());
        float[] vector = new float[dimensions];
        double norm = 0d;
        for (int i = 0; i < dimensions; i++) {
            vector[i] = (float) (random.nextDouble() * 2 - 1); // [-1, 1]
            norm += (double) vector[i] * vector[i];
        }
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < dimensions; i++) {
                vector[i] = (float) (vector[i] / norm);
            }
        }
        return new Embedding(vector);
    }

    @Override
    public int dimensions() {
        return dimensions;
    }
}
