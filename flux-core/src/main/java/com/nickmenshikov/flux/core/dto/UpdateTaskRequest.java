package com.nickmenshikov.flux.core.dto;

import com.nickmenshikov.flux.core.model.Priority;
import com.nickmenshikov.flux.core.model.Status;
import com.nickmenshikov.flux.core.model.Task;

import java.time.Instant;
import java.util.Optional;

public record UpdateTaskRequest(String title, String description, Priority priority, Status status, Instant dueDate) {

    public void applyTo(Task task) {
        Optional.ofNullable(title).ifPresent(task::setTitle);
        Optional.ofNullable(description).ifPresent(task::setDescription);
        Optional.ofNullable(priority).ifPresent(task::setPriority);
        Optional.ofNullable(status).ifPresent(task::setStatus);
        task.setDueDate(dueDate);
    }
}
