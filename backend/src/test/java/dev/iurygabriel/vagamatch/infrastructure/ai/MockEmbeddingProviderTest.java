package dev.iurygabriel.vagamatch.infrastructure.ai;

import dev.iurygabriel.vagamatch.shared.domain.Embedding;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class MockEmbeddingProviderTest {

    private final MockEmbeddingProvider provider = new MockEmbeddingProvider(1536);

    @Test
    @DisplayName("gera vetor com a dimensão configurada")
    void gera_vetor_com_a_dimensao_configurada() {
        Embedding embedding = provider.embed("React, Node e TypeScript");
        assertThat(embedding.dimensions()).isEqualTo(1536);
    }

    @Test
    @DisplayName("é determinístico: mesmo texto gera o mesmo vetor")
    void e_deterministico() {
        assertThat(provider.embed("java spring boot"))
                .isEqualTo(provider.embed("java spring boot"));
    }

    @Test
    @DisplayName("similaridade de cosseno consigo mesmo é ~1.0")
    void similaridade_consigo_mesmo_e_um() {
        Embedding embedding = provider.embed("angular");
        assertThat(embedding.cosineSimilarity(embedding)).isCloseTo(1.0, within(1e-6));
    }

    @Test
    @DisplayName("textos diferentes geram vetores diferentes")
    void textos_diferentes_geram_vetores_diferentes() {
        assertThat(provider.embed("react")).isNotEqualTo(provider.embed("java"));
    }
}
