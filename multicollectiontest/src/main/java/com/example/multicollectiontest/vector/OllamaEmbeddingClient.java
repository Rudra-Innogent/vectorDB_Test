package com.example.multicollectiontest.vector;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OllamaEmbeddingClient {

    private final WebClient webClient;

    @SuppressWarnings("unchecked")
    public List<Double> embed(String text) {

        Map<String, Object> body = Map.of(
                "model", "nomic-embed-text",
                "prompt", text
        );

        Map<String, Object> response = webClient.post()
                .uri("/api/embeddings")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("embedding")) {
            throw new RuntimeException("Ollama embedding failed");
        }

        return (List<Double>) response.get("embedding");
    }
}
