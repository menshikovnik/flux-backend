package com.nickmenshikov.tasktracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RegistrationRequest (String username, String password, String confirmPassword) {
}
