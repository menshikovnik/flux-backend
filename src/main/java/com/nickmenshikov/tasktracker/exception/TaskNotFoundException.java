package com.nickmenshikov.tasktracker.exception;

import jakarta.servlet.http.HttpServletResponse;

public class TaskNotFoundException extends AppException {
    public TaskNotFoundException(String message) {
        super(HttpServletResponse.SC_NOT_FOUND, "task_not_found", message);
    }
}
