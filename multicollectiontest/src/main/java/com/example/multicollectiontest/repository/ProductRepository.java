package com.example.multicollectiontest.repository;

import com.example.multicollectiontest.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
