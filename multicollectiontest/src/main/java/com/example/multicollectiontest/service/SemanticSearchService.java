package com.example.multicollectiontest.service;

import com.example.multicollectiontest.vector.OllamaEmbeddingClient;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SemanticSearchService {

    private final MongoTemplate mongoTemplate;
    private final OllamaEmbeddingClient embeddingClient;
    public List<Document> search(String query, String city) {

        List<Double> queryEmbedding = embeddingClient.embed(query);

        // 1) Independent vector search on users
        Document userVector = new Document("$vectorSearch",
                new Document("index", "user_vector_index")
                        .append("path", "embedding")
                        .append("queryVector", queryEmbedding)
                        .append("numCandidates", 200)
                        .append("limit", 50)
        );

        List<Document> userPipeline = new ArrayList<>();
        userPipeline.add(userVector);
        if (city != null && !city.isBlank()) {
            userPipeline.add(new Document("$match", new Document("city", new Document("$regex", "^" + city + "$").append("$options", "i"))));
        }
        userPipeline.add(new Document("$project", new Document("_id", 1).append("embedding", 1).append("profileText", 1)));

        Aggregation userAgg = Aggregation.newAggregation(userPipeline.stream().map(d -> (AggregationOperation) ctx -> d).toList());
        List<Document> userResults = mongoTemplate.aggregate(userAgg, "users", Document.class).getMappedResults();

        System.out.println("=== Independent User Search Results ===");
        // redact embedding from logs: only print identifier and profileText
        userResults.forEach(d -> {
            Document safe = new Document();
            safe.put("_id", d.get("_id"));
            safe.put("profileText", d.get("profileText"));
            System.out.println(safe.toJson());
        });

        // 2) Independent vector search on products
        Document productVector = new Document("$vectorSearch",
                new Document("index", "product_vector_index")
                        .append("path", "embedding")
                        .append("queryVector", queryEmbedding)
                        .append("numCandidates", 200)
                        .append("limit", 50)
        );

        List<Document> prodPipeline = new ArrayList<>();
        prodPipeline.add(productVector);
        prodPipeline.add(new Document("$project", new Document("_id", 1).append("title", 1).append("description", 1).append("score", 1)));

        Aggregation prodAgg = Aggregation.newAggregation(prodPipeline.stream().map(d -> (AggregationOperation) ctx -> d).toList());
        List<Document> prodResults = mongoTemplate.aggregate(prodAgg, "products", Document.class).getMappedResults();

        System.out.println("=== Independent Product Search Results ===");
        // product documents do not include embeddings by projection, but still print selective fields
        prodResults.forEach(d -> {
            Document safe = new Document();
            safe.put("_id", d.get("_id"));
            safe.put("title", d.get("title"));
            safe.put("description", d.get("description"));
            safe.put("score", d.get("score"));
            System.out.println(safe.toJson());
        });

        // 3) Two-step: compute centroid of top user embeddings then search products
        if (userResults.isEmpty()) return prodResults;

        // compute centroid of top N users (use up to 10 users)
        int dim = -1;
        int take = Math.min(10, userResults.size());
        for (int i = 0; i < take; i++) {
            List<Double> emb = (List<Double>) userResults.get(i).get("embedding");
            if (emb != null) { dim = emb.size(); break; }
        }
        if (dim <= 0) return prodResults;

        double[] sum = new double[dim];
        int count = 0;
        for (int i = 0; i < take; i++) {
            List<Double> emb = (List<Double>) userResults.get(i).get("embedding");
            if (emb == null || emb.size() != dim) continue;
            for (int j = 0; j < dim; j++) sum[j] += emb.get(j);
            count++;
        }
        if (count == 0) return prodResults;
        List<Double> centroid = new ArrayList<>(dim);
        for (int i = 0; i < dim; i++) centroid.add(sum[i] / count);

        Document productVectorFromUsers = new Document("$vectorSearch",
                new Document("index", "product_vector_index")
                        .append("path", "embedding")
                        .append("queryVector", centroid)
                        .append("numCandidates", 300)
                        .append("limit", 100)
        );

        List<Document> prodFromUsersPipeline = new ArrayList<>();
        prodFromUsersPipeline.add(productVectorFromUsers);
        prodFromUsersPipeline.add(new Document("$project", new Document("_id", 1).append("title", 1).append("description", 1).append("score", 1)));

        
        Aggregation prodFromUsersAgg = Aggregation.newAggregation(
                prodFromUsersPipeline.
                stream().
                map(
                        d -> (AggregationOperation) ctx -> d).toList()
                );
        List<Document> prodFromUsersResults = mongoTemplate.aggregate(prodFromUsersAgg, "products", Document.class).getMappedResults();

        System.out.println("=== Product Search Using Users' Embeddings (Two-step) ===");
        prodFromUsersResults.forEach(d -> {
            Document safe = new Document();
            safe.put("_id", d.get("_id"));
            safe.put("title", d.get("title"));
            safe.put("description", d.get("description"));
            safe.put("score", d.get("score"));
            System.out.println(safe.toJson());
        });

        return prodFromUsersResults;
    }
}
