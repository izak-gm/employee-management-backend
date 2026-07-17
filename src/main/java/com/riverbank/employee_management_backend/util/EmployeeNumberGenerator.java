package com.riverbank.employee_management_backend.util;

import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeNumberGenerator {

  private final EmployeeRepository employeeRepository;

  public String generate() {
    int next = employeeRepository.findTopByOrderByEmployeeNumberDesc()
          .map(Employee::getEmployeeNumber)
          .map(number -> number.replace("RBK-", ""))
          .map(Integer::parseInt)
          .map(n -> n + 1)
          .orElse(1);

    return String.format("RBK-%04d", next);
  }
}