package com.nickmenshikov.flux.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record CreateProjectRequest(
        @NotBlank(message = "Name must not be empty")
        @Size(max = 100)
        String name,

        String description,

        @NotBlank(message = "Color must not be empty")
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$")
        String color
) {
}
