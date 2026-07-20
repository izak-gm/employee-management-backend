package com.riverbank.employee_management_backend.mapper;

import com.riverbank.employee_management_backend.dto.payroll.PayrollProfileResponse;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.entity.payrolls.EmployeePayrollProfile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PayrollProfileMapper {

  public PayrollProfileResponse toResponse(EmployeePayrollProfile profile) {
    Employee emp = profile.getEmployee();

    BigDecimal gross = profile.getBasicSalary()
          .add(profile.getHouseAllowance())
          .add(profile.getTransportAllowance())
          .add(profile.getMedicalAllowance())
          .add(profile.getOtherAllowance());

    return new PayrollProfileResponse(
          profile.getId(),

          emp.getId(),
          emp.getEmployeeNumber(),
          emp.getFirstName() + " " + emp.getLastName(),
          emp.getDepartment() != null ? emp.getDepartment().getName() : null,
          emp.getPosition() != null ? emp.getPosition().getName() : null,

          profile.getBasicSalary(),
          profile.getHouseAllowance(),
          profile.getTransportAllowance(),
          profile.getMedicalAllowance(),
          profile.getOtherAllowance(),
          profile.getPensionContribution(),
          gross,

          profile.getBankName(),
          profile.getBankBranch(),
          profile.getAccountNumber(),

          profile.getKraPin(),
          profile.getShifNumber(),
          profile.getNssfNumber(),

          profile.isActive(),
          profile.getEffectiveFrom()
//                profile.getEffectiveTo()
    );
  }
}
