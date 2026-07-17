package com.riverbank.employee_management_backend.dto.position;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record PositionRequest(
      @NotBlank(message = "Department name is required")
      String name,
      String description,
      UUID department
) {
}
