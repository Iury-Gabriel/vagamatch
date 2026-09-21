package dev.iurygabriel.vagamatch.shared.application.port;

import dev.iurygabriel.vagamatch.shared.domain.Embedding;

import java.util.List;

/**
 * Porta de saída para geração de embeddings.
 * A aplicação depende desta interface; a infraestrutura fornece o adapter (OpenAI, mock, etc.).
 */
public interface EmbeddingProvider {

    Embedding embed(String text);

    default List<Embedding> embedAll(List<String> texts) {
        return texts.stream().map(this::embed).toList();
    }

    /** Dimensão dos vetores gerados por este provider. */
    int dimensions();
}
