package com.riverbank.employee_management_backend.service.payroll;

import com.riverbank.employee_management_backend.dto.payroll.*;
import com.riverbank.employee_management_backend.entity.Employee;

import java.util.List;
import java.util.UUID;

public interface PayrollService {

  // Generate payroll for one or many employees
  List<PayrollSummaryResponse> generatePayroll(GeneratePayrollRequest request, Employee generatedBy);

  // Approve a single payroll
  PayrollResponse approvePayroll(UUID payrollId, Employee approver);

  // Mark as paid
  PayrollResponse markAsPaid(UUID payrollId, MarkAsPaidRequest request, Employee markedBy);

  // Reverse
  PayrollResponse reversePayroll(UUID payrollId, ReversePayrollRequest request, Employee reversedBy);

  // Queries
  PayrollResponse getPayrollById(UUID payrollId);

  List<PayrollSummaryResponse> getPayrollsByMonthAndYear(int month, int year);

  List<PayrollSummaryResponse> getMyPayrolls(UUID employeeId);

  PayrollResponse getMyPayrollForPeriod(UUID employeeId, int month, int year);

  // Re-send payslip email
  void resendPayslip(UUID payrollId);

  // Download payslip PDF bytes
  byte[] downloadPayslip(UUID payrollId);

  List<PayrollSummaryResponse> getGeneratedPayrolls(int month, int year);

  List<PayrollResponse> bulkApprovePayroll(int month, int year, Employee approver);

  List<PayrollResponse> bulkApprovePayrollByIds(List<UUID> payrollIds, Employee approver);

  List<PayrollResponse> bulkReversePayroll(List<UUID> payrollIds, String reason, Employee reversedBy);

  byte[] generateBatchReport(int month, int year); // PDF or XLSX for review

  byte[] generateApprovedBatchReport(int month, int year);

}
