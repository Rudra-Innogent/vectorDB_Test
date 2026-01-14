package com.example.multicollectiontest.controller;

import com.example.multicollectiontest.entity.Order;
import com.example.multicollectiontest.entity.User;
import com.example.multicollectiontest.service.OrderWriteService;
import com.example.multicollectiontest.service.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/data")
public class DataController {

    private final UserWriteService userService;
    private final OrderWriteService orderService;
    private final ProductWriteService productService;

    // ---------- SINGLE USER ----------
    @PostMapping("/user")
    public User createUser(@RequestBody UserRequest request) {
        return userService.createUser(
                request.getCity(),
                request.getProfile()
        );
    }

    // ---------- SINGLE ORDER ----------
    @PostMapping("/order")
    public Order createOrder(@RequestParam String userId,
                             @RequestParam String product) {
        return orderService.createOrder(userId, product);
    }

    // ---------- BULK USERS ----------
    @PostMapping("/bulkUser")
    public List<User> bulkCreateUser(@RequestBody List<UserRequest> users) {

        List<User> savedUsers = new ArrayList<>();

        for (UserRequest user : users) {
            User saved = userService.createUser(
                    user.getCity(),
                    user.getProfile()
            );
            savedUsers.add(saved);
        }

        return savedUsers;
    }
    // ---------- BULK ORDERS FOR ONE USER ----------
    @PostMapping("/bulkOrder")
    public List<Order> bulkCreateOrders(@RequestBody BulkOrderRequest request) {

        List<Order> orders = new ArrayList<>();

        for (String product : request.getProduct()) {
            orders.add(orderService.createOrder(request.getUserId(), product));
        }
        return orders;
    }

    // ---------- SINGLE PRODUCT ----------
    @PostMapping("/product")
    public com.example.multicollectiontest.entity.Product createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getBrand(),
                request.getPrice(),
                request.getRating()
        );
    }

    // ---------- BULK PRODUCTS ----------
    @PostMapping("/bulkProduct")
    public List<com.example.multicollectiontest.entity.Product> bulkCreateProducts(@RequestBody List<ProductRequest> products) {
        List<com.example.multicollectiontest.entity.Product> saved = new ArrayList<>();
        for (ProductRequest p : products) {
            saved.add(productService.createProduct(
                    p.getTitle(), p.getDescription(), p.getCategory(), p.getBrand(), p.getPrice(), p.getRating()
            ));
        }
        return saved;
    }

}

@Data
class UserRequest {
    private String city;
    private String profile;
}

@Data
class BulkOrderRequest {
    private String userId;
    private List<String> product;
}

@Data
class ProductRequest {
    private String title;
    private String description;
    private String category;
    private String brand;
    private Double price;
    private Double rating;
}
