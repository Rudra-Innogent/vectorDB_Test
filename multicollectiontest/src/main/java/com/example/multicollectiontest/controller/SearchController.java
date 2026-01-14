package com.example.multicollectiontest.controller;

import com.example.multicollectiontest.service.SemanticSearchService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SemanticSearchService service;

    @GetMapping
    public List<Document> search(@RequestParam String query,
                                 @RequestParam(required = false) String city) {
        return service.search(query, city);
    }
}
