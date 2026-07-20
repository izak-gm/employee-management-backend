package com.riverbank.employee_management_backend.dto.payroll;

import jakarta.validation.constraints.NotBlank;

public record ReversePayrollRequest(

        @NotBlank(message = "Reversal reason is required")
        String reason
) {}
