package com.riverbank.employee_management_backend.dto.payroll;

import com.riverbank.employee_management_backend.enums.payrolls.PayrollStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PayrollResponse(
        UUID id,
        String payrollNumber,

        // Employee
        UUID employeeId,
        String employeeNumber,
        String employeeFullName,
        String department,
        String position,

        // Period
        Integer payrollMonth,
        Integer payrollYear,
        LocalDate payrollDate,

        // Financials
        BigDecimal grossPay,
        BigDecimal taxablePay,
        BigDecimal totalEarnings,
        BigDecimal totalDeductions,
        BigDecimal netPay,

        // Statutory breakdown
        BigDecimal paye,
        BigDecimal nssf,
        BigDecimal shif,
        BigDecimal housingLevy,
        BigDecimal employerNssf,
        BigDecimal employerShif,

        // Line items
        List<PayrollEarningResponse> earnings,
        List<PayrollDeductionResponse> deductions,

        // Status & audit
        PayrollStatus status,
        String generatedBy,
        LocalDateTime generatedAt,
        String approvedBy,
        LocalDateTime approvedAt,
        String reversedBy,
        LocalDateTime reversedAt,
        String reversalReason,

        // Payment
        LocalDate paymentDate,
        String paymentReference,
        String remarks
) {}
