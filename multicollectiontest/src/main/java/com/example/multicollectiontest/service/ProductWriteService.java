package com.example.multicollectiontest.service;

import com.example.multicollectiontest.entity.Product;
import com.example.multicollectiontest.repository.ProductRepository;
import com.example.multicollectiontest.vector.OllamaEmbeddingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductWriteService {

    private final ProductRepository productRepository;
    private final OllamaEmbeddingClient embeddingClient;

    public Product createProduct(String title,
                                 String description,
                                 String category,
                                 String brand,
                                 Double price,
                                 Double rating) {

        Product p = Product.builder()
                .title(title)
                .description(description)
                .category(category)
                .brand(brand)
                .price(price)
                .rating(rating)
                .embedding(embeddingClient.embed(title + " " + description))
                .build();

        return productRepository.save(p);
    }
}
