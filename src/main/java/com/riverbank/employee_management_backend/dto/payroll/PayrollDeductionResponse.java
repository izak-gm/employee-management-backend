package com.riverbank.employee_management_backend.dto.payroll;

import java.math.BigDecimal;
import java.util.UUID;

public record PayrollDeductionResponse(
        UUID id,
        String deductionType,
        boolean statutory,
        BigDecimal amount,
        String remarks
) {}
