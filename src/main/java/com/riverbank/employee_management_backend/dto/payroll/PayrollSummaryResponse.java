package com.riverbank.employee_management_backend.dto.payroll;

import com.riverbank.employee_management_backend.enums.payrolls.PayrollStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Lightweight — used in list/table views, no line items
public record PayrollSummaryResponse(
        UUID id,
        String payrollNumber,
        UUID employeeId,
        String employeeFullName,
        String employeeNumber,
        String department,
        Integer payrollMonth,
        Integer payrollYear,
        BigDecimal grossPay,
        BigDecimal netPay,
        BigDecimal totalDeductions,
        PayrollStatus status,
        LocalDate payrollDate,
        LocalDate paymentDate
) {}
