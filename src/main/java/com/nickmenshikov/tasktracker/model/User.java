package com.nickmenshikov.tasktracker.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private Instant createdAt;
}
