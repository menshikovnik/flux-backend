package com.nickmenshikov.flux.search.controller;

import com.nickmenshikov.flux.search.document.TaskDocument;
import com.nickmenshikov.flux.search.repository.TaskSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final TaskSearchRepository repository;

    @GetMapping(value = "/tasks")
    public List<TaskDocument> search(@RequestParam String q) {
        return null;
    }

}
