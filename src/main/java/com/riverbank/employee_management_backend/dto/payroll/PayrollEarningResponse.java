package com.riverbank.employee_management_backend.dto.payroll;

import java.math.BigDecimal;
import java.util.UUID;

public record PayrollEarningResponse(
        UUID id,
        String earningType,
        boolean taxable,
        BigDecimal amount,
        String remarks
) {}
