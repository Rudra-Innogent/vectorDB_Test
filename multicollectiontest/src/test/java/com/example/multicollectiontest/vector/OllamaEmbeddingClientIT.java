package com.example.multicollectiontest.vector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Integration-Test")
@SpringBootTest
class OllamaEmbeddingClientIT {

    @Autowired
    private OllamaEmbeddingClient embeddingClient;

    @Test
    void embed_shouldReturnRealEmbeddingFromOllama() {

        List<Double> embedding = embeddingClient.embed("Hello Ollama");

        assertNotNull(embedding, "Embedding should not be null");
        assertFalse(embedding.isEmpty(), "Embedding should not be empty");

        assertTrue(
                embedding.size() > 100,
                "Embedding dimension looks incorrect"
        );
    }
}
