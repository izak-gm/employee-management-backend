package com.riverbank.employee_management_backend.dto.payroll;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EarningTypeRequest(

      @NotBlank(message = "Name is required")
      String name,

      String description,

      @NotNull(message = "Taxable flag is required")
      Boolean taxable,

      @NotNull(message = "Fixed flag is required")
      Boolean fixed,

      Integer displayOrder
) {
}