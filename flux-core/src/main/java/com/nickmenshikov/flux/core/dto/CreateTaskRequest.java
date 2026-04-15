package com.nickmenshikov.flux.core.dto;

import com.nickmenshikov.flux.core.model.Priority;
import com.nickmenshikov.flux.core.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTaskRequest(
        @NotBlank(message = "Title must not be empty")
        String title,

        String description,

        @NotNull(message = "Priority must not be empty")
        Priority priority,

        @NotNull(message = "Status must not be empty")
        Status status,

        Long projectId
) {
}
