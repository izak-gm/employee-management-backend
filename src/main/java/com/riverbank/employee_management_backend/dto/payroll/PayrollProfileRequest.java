package com.riverbank.employee_management_backend.dto.payroll;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PayrollProfileRequest(

        @NotNull(message = "Employee ID is required")
        UUID employeeId,

        // ── Salary ────────────────────────────────────────────────────────────

        @NotNull(message = "Basic salary is required")
        @DecimalMin(value = "0.01", message = "Basic salary must be greater than zero")
        @Digits(integer = 16, fraction = 2, message = "Invalid salary format")
        BigDecimal basicSalary,

        @DecimalMin(value = "0.00", message = "House allowance cannot be negative")
        @Digits(integer = 16, fraction = 2)
        BigDecimal houseAllowance,

        @DecimalMin(value = "0.00", message = "Transport allowance cannot be negative")
        @Digits(integer = 16, fraction = 2)
        BigDecimal transportAllowance,

        @DecimalMin(value = "0.00", message = "Medical allowance cannot be negative")
        @Digits(integer = 16, fraction = 2)
        BigDecimal medicalAllowance,

        @DecimalMin(value = "0.00", message = "Other allowance cannot be negative")
        @Digits(integer = 16, fraction = 2)
        BigDecimal otherAllowance,

        @DecimalMin(value = "0.00", message = "Pension contribution cannot be negative")
        @Digits(integer = 16, fraction = 2)
        BigDecimal pensionContribution,

        // ── Bank details ──────────────────────────────────────────────────────

        @NotBlank(message = "Bank name is required")
        String bankName,

        String bankBranch,

        @NotBlank(message = "Account number is required")
        String accountNumber,

        // ── Statutory numbers ─────────────────────────────────────────────────

        @NotBlank(message = "KRA PIN is required")
        @Pattern(regexp = "^[A-Z]\\d{9}[A-Z]$", message = "Invalid KRA PIN format (e.g. A123456789Z)")
        String kraPin,

        @NotBlank(message = "SHIF number is required")
        String shifNumber,

        @NotBlank(message = "NSSF number is required")
        String nssfNumber,

        // ── Effective dates ───────────────────────────────────────────────────

        LocalDate effectiveFrom,

        LocalDate effectiveTo
) {}
