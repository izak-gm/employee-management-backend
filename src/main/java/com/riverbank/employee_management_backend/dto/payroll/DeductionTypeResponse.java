package com.riverbank.employee_management_backend.dto.payroll;

import com.riverbank.employee_management_backend.enums.payrolls.DeductionCalculationType;

import java.math.BigDecimal;
import java.util.UUID;

public record DeductionTypeResponse(
      UUID id,
      String name,
      String description,
      boolean statutory,
      boolean taxable,
      boolean active,
      DeductionCalculationType calculationType,
      BigDecimal fixedAmount,
      BigDecimal percentage,
      int displayOrder
) {
}