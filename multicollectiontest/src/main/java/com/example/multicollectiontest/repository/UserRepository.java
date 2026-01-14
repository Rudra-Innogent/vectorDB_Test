package com.example.multicollectiontest.repository;

import com.example.multicollectiontest.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
