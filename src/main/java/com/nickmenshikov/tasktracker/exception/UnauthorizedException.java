package com.nickmenshikov.tasktracker.exception;

import jakarta.servlet.http.HttpServletResponse;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(HttpServletResponse.SC_UNAUTHORIZED, "unauthorized", message);
    }
}
