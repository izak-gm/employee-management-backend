package com.riverbank.employee_management_backend.dto.bulk;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record BulkReverseRequest(
      @NotEmpty(message = "payrollIds must not be empty")
      List<UUID> payrollIds,

      @NotBlank(message = "reason is required")
      String reason
) {
}