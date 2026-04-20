package com.nickmenshikov.flux.contracts;


import java.time.Instant;

public record TaskEvent(
        TaskEventType type,
        Long taskId,
        Long userId,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Instant createdAt
) {
}
