package com.nickmenshikov.flux.core.exception;

import org.springframework.http.HttpStatus;

public class ProjectNotFoundException extends AppException {
    public ProjectNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "project_not_found", message);
    }
}
