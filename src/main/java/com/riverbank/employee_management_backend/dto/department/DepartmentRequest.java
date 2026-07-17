package com.riverbank.employee_management_backend.dto.department;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
      @NotBlank(message = "Department name is required")
      String name,

      String description
) {
}
