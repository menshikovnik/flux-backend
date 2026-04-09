package com.nickmenshikov.tasktracker.exception;

import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends AppException {
    public TaskNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "task_not_found", message);
    }
}
