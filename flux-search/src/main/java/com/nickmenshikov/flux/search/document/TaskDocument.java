package com.nickmenshikov.flux.search.document;

import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;

@Document(indexName = "tasks")
public record TaskDocument(
        @Id
        String id,
        Long taskId,
        Long userId,
        String title,
        String description,
        String status,
        String priority,
        Instant createdAt
) {}
