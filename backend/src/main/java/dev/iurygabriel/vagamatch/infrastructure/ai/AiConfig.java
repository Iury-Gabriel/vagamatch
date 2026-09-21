package dev.iurygabriel.vagamatch.infrastructure.ai;

import dev.iurygabriel.vagamatch.shared.application.port.EmbeddingProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seleciona o adapter de IA por configuração (vagamatch.ai.provider).
 * Padrão: mock (roda sem custo). Com provider=openai, usa a API real.
 */
@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiConfig {

    @Bean
    @ConditionalOnProperty(prefix = "vagamatch.ai", name = "provider", havingValue = "openai")
    EmbeddingProvider openAiEmbeddingProvider(AiProperties props) {
        return new OpenAiEmbeddingAdapter(props);
    }

    @Bean
    @ConditionalOnProperty(prefix = "vagamatch.ai", name = "provider", havingValue = "mock", matchIfMissing = true)
    EmbeddingProvider mockEmbeddingProvider(AiProperties props) {
        return new MockEmbeddingProvider(props.embeddingDimensions());
    }
}
