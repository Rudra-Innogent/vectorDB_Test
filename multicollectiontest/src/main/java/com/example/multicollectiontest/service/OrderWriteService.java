package com.example.multicollectiontest.service;

import com.example.multicollectiontest.entity.Order;
import com.example.multicollectiontest.repository.OrderRepository;
import com.example.multicollectiontest.vector.OllamaEmbeddingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderWriteService {

    private final OrderRepository orderRepository;
    private final OllamaEmbeddingClient embeddingClient;

    public Order createOrder(String userId, String productText) {

        Order order = Order.builder()
                .userId(userId)
                .productText(productText)
                .embedding(embeddingClient.embed(productText))
                .build();

        return orderRepository.save(order);
    }
}
