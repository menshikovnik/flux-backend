package com.nickmenshikov.flux.core.exception;

import org.springframework.http.HttpStatus;

public class ProjectNameAlreadyTakenException extends AppException {
    public ProjectNameAlreadyTakenException(String message) {
        super(HttpStatus.CONFLICT, "project_name_already_taken", message);
    }
}
