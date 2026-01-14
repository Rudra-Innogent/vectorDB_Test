package com.example.multicollectiontest.service;

import com.example.multicollectiontest.entity.User;
import com.example.multicollectiontest.repository.UserRepository;
import com.example.multicollectiontest.vector.OllamaEmbeddingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserWriteService {

    private final UserRepository userRepository;
    private final OllamaEmbeddingClient embeddingClient;

    public User createUser(String city, String profileText) {

        User user = User.builder()
                .city(city)
                .profileText(profileText)
                .embedding(embeddingClient.embed(profileText))
                .build();

        return userRepository.save(user);
    }
}
