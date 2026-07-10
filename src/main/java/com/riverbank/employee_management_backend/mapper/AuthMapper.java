package com.riverbank.employee_management_backend.mapper;

import com.riverbank.employee_management_backend.dto.auth.EmployeeResponse;
import com.riverbank.employee_management_backend.entity.Employee;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthMapper {
  private final PasswordEncoder passwordEncoder;

  public EmployeeResponse toEmployeeResponse(Employee employee) {
    return new EmployeeResponse(
          employee.getId(),
          employee.getFirstName(),
          employee.getLastName(),
          employee.getEmail(),
          employee.getPhoneNumber(),
          employee.getRole()
    );
  }
}
