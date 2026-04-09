package com.nickmenshikov.tasktracker.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(String message) {
        super(HttpStatus.UNAUTHORIZED, "user_not_found", message);
    }
}
