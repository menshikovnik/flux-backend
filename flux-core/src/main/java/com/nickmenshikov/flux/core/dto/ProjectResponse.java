package com.nickmenshikov.flux.core.dto;

import com.nickmenshikov.flux.core.model.Project;

import java.time.Instant;

public record ProjectResponse(
        Long id,

        String name,

        String description,

        String color,

        Boolean isArchived,

        Instant createdAt
) {

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getColor(),
                project.getIsArchived(),
                project.getCreatedAt()
        );
    }
}
