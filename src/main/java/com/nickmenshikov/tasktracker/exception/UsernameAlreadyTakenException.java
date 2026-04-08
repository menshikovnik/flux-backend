package com.nickmenshikov.tasktracker.exception;

import jakarta.servlet.http.HttpServletResponse;

public class UsernameAlreadyTakenException extends AppException {
    public UsernameAlreadyTakenException(String message) {
        super(HttpServletResponse.SC_CONFLICT, "username_already_taken", message);
    }
}
