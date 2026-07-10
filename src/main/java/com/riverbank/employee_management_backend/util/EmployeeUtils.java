package com.riverbank.employee_management_backend.util;

import com.riverbank.employee_management_backend.dto.auth.EmployeeResponse;
import com.riverbank.employee_management_backend.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeUtils {
  public EmployeeResponse toEmployeeResponse(Employee e) {
    return new EmployeeResponse(e.getId(), e.getFirstName(), e.getLastName(),
          e.getEmail(), e.getPhoneNumber(), e.getRole());
  }

}
