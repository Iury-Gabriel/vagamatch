package dev.iurygabriel.vagamatch.infrastructure.ai;

import dev.iurygabriel.vagamatch.shared.application.port.EmbeddingProvider;
import dev.iurygabriel.vagamatch.shared.domain.DomainException;
import dev.iurygabriel.vagamatch.shared.domain.Embedding;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Adapter que gera embeddings via API compatível com OpenAI (POST /embeddings).
 * Ativado com vagamatch.ai.provider=openai e uma API key configurada.
 */
public class OpenAiEmbeddingAdapter implements EmbeddingProvider {

    private final RestClient restClient;
    private final String model;
    private final int dimensions;

    public OpenAiEmbeddingAdapter(AiProperties props) {
        this.restClient = RestClient.builder()
                .baseUrl(props.baseUrl())
                .defaultHeader("Authorization", "Bearer " + props.apiKey())
                .build();
        this.model = props.embeddingModel();
        this.dimensions = props.embeddingDimensions();
    }

    @Override
    public Embedding embed(String text) {
        EmbeddingResponse response = restClient.post()
                .uri("/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new EmbeddingRequest(model, text))
                .retrieve()
                .body(EmbeddingResponse.class);

        if (response == null || response.data() == null || response.data().isEmpty()) {
            throw new DomainException("Resposta de embedding vazia do provedor de IA");
        }
        return new Embedding(response.data().get(0).embedding());
    }

    @Override
    public int dimensions() {
        return dimensions;
    }

    private record EmbeddingRequest(String model, String input) {
    }

    private record EmbeddingResponse(List<Item> data) {
        private record Item(float[] embedding) {
        }
    }
}
