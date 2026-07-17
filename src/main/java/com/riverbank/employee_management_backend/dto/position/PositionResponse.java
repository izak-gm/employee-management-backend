package com.riverbank.employee_management_backend.dto.position;

import java.util.UUID;

public record PositionResponse(
      UUID id,
      String name,
      String description,
      boolean active
) {
}
