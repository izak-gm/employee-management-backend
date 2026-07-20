package com.riverbank.employee_management_backend.dto.payroll;

import jakarta.validation.constraints.NotBlank;

public record MarkAsPaidRequest(

        @NotBlank(message = "Payment reference is required")
        String paymentReference
) {}
