package com.riverbank.employee_management_backend.dto.payroll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PayrollProfileResponse(

      UUID id,

      // ── Employee ──────────────────────────────────────────────────────────
      UUID employeeId,
      String employeeNumber,
      String employeeFullName,
      String department,
      String position,

      // ── Salary ────────────────────────────────────────────────────────────
      BigDecimal basicSalary,
      BigDecimal houseAllowance,
      BigDecimal transportAllowance,
      BigDecimal medicalAllowance,
      BigDecimal otherAllowance,
      BigDecimal pensionContribution,

      // ── Computed gross (convenience field) ────────────────────────────────
      BigDecimal grossSalary,

      // ── Bank details ──────────────────────────────────────────────────────
      String bankName,
      String bankBranch,
      String accountNumber,

      // ── Statutory numbers ─────────────────────────────────────────────────
      String kraPin,
      String shifNumber,
      String nssfNumber,

      // ── Status ────────────────────────────────────────────────────────────
      boolean active,
      LocalDate effectiveFrom
//        LocalDate effectiveTo
) {
}
