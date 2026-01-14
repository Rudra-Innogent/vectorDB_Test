//package com.example.multicollectiontest.service;
//
//import com.example.multicollectiontest.vector.OllamaEmbeddingClient;
//import org.bson.Document;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.aggregation.Aggregation;
//import org.springframework.data.mongodb.core.aggregation.AggregationResults;
//
//import java.util.List;
//
//import static org.junit.Assert.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@RunWith(MockitoJUnitRunner.class) // 🔑 Mockito + JUnit 4
//public class SemanticSearchServiceTest2 {
//
//    @Mock
//    private MongoTemplate mongoTemplate;
//
//    @Mock
//    private OllamaEmbeddingClient embeddingClient;
//
//    @InjectMocks
//    private SemanticSearchService service;
//
//    @Before
//    public void setUp() {
//        // Mockito runner initializes mocks
//    }
//
//    @Test
//    public void search_returnsProducts_whenUsersExist() {
//        // given
//        List<Double> embedding = List.of(0.1, 0.2, 0.3);
//        when(embeddingClient.embed("query")).thenReturn(embedding);
//
//        AggregationResults<Document> users =
//                new AggregationResults<>(
//                        List.of(new Document("_id", "u1").append("embedding", embedding)),
//                        new Document()
//                );
//
//        AggregationResults<Document> products =
//                new AggregationResults<>(
//                        List.of(new Document("_id", "p1")),
//                        new Document()
//                );
//
//        when(mongoTemplate.aggregate(any(Aggregation.class), eq("users"), eq(Document.class)))
//                .thenReturn(users);
//
//        when(mongoTemplate.aggregate(any(Aggregation.class), eq("products"), eq(Document.class)))
//                .thenReturn(products);
//
//        // when
//        List<Document> result = service.search("query", "Delhi");
//
//        // then
//        assertEquals(1, result.size());
//        assertEquals("p1", result.get(0).get("_id"));
//    }
//}
