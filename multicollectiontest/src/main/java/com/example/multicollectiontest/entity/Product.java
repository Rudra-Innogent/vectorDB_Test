package com.example.multicollectiontest.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("products")
public class Product {

    @Id
    private String id;

    private String title;

    private String description;

    private String category;

    private String brand;

    private Double price;

    private Double rating;

    private List<String> images;

    private List<String> specs;

    private List<Double> embedding;
}
