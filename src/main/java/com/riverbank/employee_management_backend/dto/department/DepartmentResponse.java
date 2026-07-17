package com.riverbank.employee_management_backend.dto.department;

import java.util.UUID;

public record DepartmentResponse(
      UUID id,
      String name,
      String description,
      boolean active
) {
}
