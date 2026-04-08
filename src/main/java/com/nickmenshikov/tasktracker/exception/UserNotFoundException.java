package com.nickmenshikov.tasktracker.exception;

import jakarta.servlet.http.HttpServletResponse;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(String message) {
        super(HttpServletResponse.SC_UNAUTHORIZED, "user_not_found", message);
    }
}
