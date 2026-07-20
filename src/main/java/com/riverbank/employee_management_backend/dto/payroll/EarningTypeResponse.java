package com.riverbank.employee_management_backend.dto.payroll;

import java.util.UUID;

public record EarningTypeResponse(
      UUID id,
      String name,
      String description,
      boolean taxable,
      boolean fixed,
      boolean active,
      int displayOrder
) {
}