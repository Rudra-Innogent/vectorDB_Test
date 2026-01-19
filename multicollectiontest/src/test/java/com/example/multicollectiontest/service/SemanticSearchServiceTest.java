package com.example.multicollectiontest.service;

import com.example.multicollectiontest.vector.OllamaEmbeddingClient;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 🔑 Enables Mockito in JUnit 5
class SemanticSearchServiceTest {

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    OllamaEmbeddingClient embeddingClient;

    @InjectMocks
    SemanticSearchService service;

    @BeforeEach
    void setUp() {}

    @Test
    void search_shouldReturnProductResults_whenUsersExist() {

        List<Double> embedding = List.of(0.1, 0.2, 0.3);
        when(embeddingClient.embed("query")).thenReturn(embedding);

        Document userDoc = new Document("_id", "u1")
                .append("embedding", embedding)
                .append("profileText", "Java Developer");

        AggregationResults<Document> userAggResult =
                new AggregationResults<>(List.of(userDoc), new Document());

        AggregationResults<Document> productAggResult =
                new AggregationResults<>(
                        List.of(new Document("_id", "p1").append("title", "Laptop")),
                        new Document()
                );

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("users"), eq(Document.class)))
                .thenReturn(userAggResult);

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("products"), eq(Document.class)))
                .thenReturn(productAggResult);

        List<Document> result = service.search("query", "Delhi");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("p1", result.get(0).get("_id"));

        verify(embeddingClient).embed("query");
        verify(mongoTemplate, atLeastOnce())
                .aggregate(any(Aggregation.class), anyString(), eq(Document.class));
    }

    @Test
    void search_shouldReturnProducts_whenNoUsersFound() {

        when(embeddingClient.embed(anyString()))
                .thenReturn(List.of(0.1, 0.2));

        AggregationResults<Document> emptyUsers =
                new AggregationResults<>(List.of(), new Document());

        AggregationResults<Document> products =
                new AggregationResults<>(
                        List.of(new Document("_id", "p2")),
                        new Document()
                );

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("users"), eq(Document.class)))
                .thenReturn(emptyUsers);

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("products"), eq(Document.class)))
                .thenReturn(products);

        List<Document> result = service.search("query", null);

        assertEquals(1, result.size());
        assertEquals("p2", result.get(0).get("_id"));
    }
}
