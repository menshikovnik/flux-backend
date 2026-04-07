package com.nickmenshikov.tasktracker.dto;

import com.nickmenshikov.tasktracker.model.Priority;
import com.nickmenshikov.tasktracker.model.Status;

public record CreateTaskRequest(String title, String description, Priority priority, Long creatorId, Status status) {
}
