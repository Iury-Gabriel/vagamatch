package dev.iurygabriel.vagamatch.infrastructure.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuração dos adapters de IA (vagamatch.ai.*). */
@ConfigurationProperties("vagamatch.ai")
public record AiProperties(
        String provider,
        String apiKey,
        String baseUrl,
        String embeddingModel,
        Integer embeddingDimensions,
        String chatModel
) {
    public AiProperties {
        if (provider == null || provider.isBlank()) {
            provider = "mock";
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "https://api.openai.com/v1";
        }
        if (embeddingModel == null || embeddingModel.isBlank()) {
            embeddingModel = "text-embedding-3-small";
        }
        if (embeddingDimensions == null) {
            embeddingDimensions = 1536;
        }
    }
}
