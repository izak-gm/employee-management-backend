package com.riverbank.employee_management_backend.dto.payroll.PAYE;

import java.math.BigDecimal;

public record TaxCalculation(
      BigDecimal incomeTax,
      BigDecimal personalRelief,
      BigDecimal paye
) {
}