package com.riverbank.employee_management_backend.service.payroll;


import com.riverbank.employee_management_backend.dto.payroll.EmployeePayrollProfileRequest;
import com.riverbank.employee_management_backend.dto.payroll.EmployeePayrollProfileResponse;

import java.util.List;
import java.util.UUID;

public interface EmployeePayrollProfileService {

  EmployeePayrollProfileResponse create(EmployeePayrollProfileRequest request);

  EmployeePayrollProfileResponse update(
        UUID id,
        EmployeePayrollProfileRequest request
  );

  EmployeePayrollProfileResponse findById(UUID id);

  EmployeePayrollProfileResponse findByEmployee(UUID employeeId);

  List<EmployeePayrollProfileResponse> findAll();

  void delete(UUID id);
}