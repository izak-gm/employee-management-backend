package com.riverbank.employee_management_backend.mapper;

import com.riverbank.employee_management_backend.dto.payroll.PayrollDeductionResponse;
import com.riverbank.employee_management_backend.dto.payroll.PayrollEarningResponse;
import com.riverbank.employee_management_backend.dto.payroll.PayrollResponse;
import com.riverbank.employee_management_backend.dto.payroll.PayrollSummaryResponse;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.entity.payrolls.Payroll;
import com.riverbank.employee_management_backend.entity.payrolls.PayrollDeduction;
import com.riverbank.employee_management_backend.entity.payrolls.PayrollEarning;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class PayrollMapper {

  public PayrollResponse toResponse(Payroll p) {
    Employee emp = p.getEmployee();

    return new PayrollResponse(
          p.getId(),
          p.getPayrollNumber(),

          emp.getId(),
          emp.getEmployeeNumber(),
          fullName(emp),
          emp.getDepartment() != null ? emp.getDepartment().getName() : null,
          emp.getPosition() != null ? emp.getPosition().getName() : null,

          p.getPayrollMonth(),
          p.getPayrollYear(),
          p.getPayrollDate(),

          p.getGrossPay(),
          p.getTaxablePay(),
          p.getTotalEarnings(),
          p.getTotalDeductions(),
          p.getNetPay(),

          p.getPaye(),
          p.getNssf(),
          p.getShif(),
          p.getHousingLevy(),
          p.getEmployerNssf(),
          p.getEmployerShif(),

          mapEarnings(p.getEarnings()),
          mapDeductions(p.getDeductions()),

          p.getStatus(),
          p.getGeneratedBy() != null ? fullName(p.getGeneratedBy()) : null,
          p.getGeneratedAt(),
          p.getApprovedBy() != null ? fullName(p.getApprovedBy()) : null,
          p.getApprovedAt(),
          p.getReversedBy() != null ? fullName(p.getReversedBy()) : null,
          p.getReversedAt(),
          p.getReversalReason(),

          p.getPaymentDate(),
          p.getPaymentReference(),
          p.getRemarks()
    );
  }

  public PayrollSummaryResponse toSummary(Payroll p) {
    Employee emp = p.getEmployee();
    return new PayrollSummaryResponse(
          p.getId(),
          p.getPayrollNumber(),
          emp.getId(),
          fullName(emp),
          emp.getEmployeeNumber(),
          emp.getDepartment() != null ? emp.getDepartment().getName() : null,
          p.getPayrollMonth(),
          p.getPayrollYear(),
          p.getGrossPay(),
          p.getNetPay(),
          p.getTotalDeductions(),
          p.getStatus(),
          p.getPayrollDate(),
          p.getPaymentDate(),
          p.getPersonalRelief(),
          p.getIncomeTax(),
          p.getStatutoryDeductions(),
          p.getPayAfterStatutoryDeductions()
    );
  }

  private List<PayrollEarningResponse> mapEarnings(Collection<PayrollEarning> earnings) {
    if (earnings == null) return Collections.emptyList();
    return earnings.stream().map(e -> new PayrollEarningResponse(
          e.getId(),
          e.getEarningType().getName(),
          e.getEarningType().isTaxable(),
          e.getAmount(),
          e.getRemarks()
    )).toList();
  }

  private List<PayrollDeductionResponse> mapDeductions(Collection<PayrollDeduction> deductions) {
    if (deductions == null) return Collections.emptyList();
    return deductions.stream().map(d -> new PayrollDeductionResponse(
          d.getId(),
          d.getDeductionType().getName(),
          d.getDeductionType().isStatutory(),
          d.getAmount(),
          d.getRemarks()
    )).toList();
  }

  private String fullName(Employee e) {
    return (e.getFirstName() + " " + e.getLastName()).trim();
  }
}
