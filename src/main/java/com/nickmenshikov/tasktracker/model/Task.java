package com.nickmenshikov.tasktracker.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Task {
    private Long id;
    private String title;
    private String description;
    private Instant createdAt;
    private Status status;
    private Priority priority;
    private Long creatorId;
}
