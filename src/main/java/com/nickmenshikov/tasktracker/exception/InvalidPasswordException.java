package com.nickmenshikov.tasktracker.exception;

import jakarta.servlet.http.HttpServletResponse;

public class InvalidPasswordException extends AppException {
    public InvalidPasswordException(String message) {
        super(HttpServletResponse.SC_UNAUTHORIZED, "invalid_password", message);
    }
}
