package com.riverbank.employee_management_backend.dto.payroll;

import com.riverbank.employee_management_backend.enums.payrolls.DeductionCalculationType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record DeductionTypeRequest(

      @NotBlank(message = "Name is required")
      String name,

      String description,

      @NotNull(message = "Statutory flag is required")
      Boolean statutory,

      @NotNull(message = "Taxable flag is required")
      Boolean taxable,

      @NotNull(message = "Calculation type is required")
      DeductionCalculationType calculationType,

      // Required when calculationType = FIXED
      @DecimalMin(value = "0.00", message = "Fixed amount cannot be negative")
      @Digits(integer = 16, fraction = 2)
      BigDecimal fixedAmount,

      // Required when calculationType = PERCENTAGE
      @DecimalMin(value = "0.00", message = "Percentage cannot be negative")
      @DecimalMax(value = "100.00", message = "Percentage cannot exceed 100")
      @Digits(integer = 3, fraction = 2)
      BigDecimal percentage,

      Integer displayOrder
) {
}