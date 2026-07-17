package com.riverbank.employee_management_backend.dto.auth;

import com.riverbank.employee_management_backend.enums.EmployeeStatus;
import com.riverbank.employee_management_backend.enums.Gender;
import com.riverbank.employee_management_backend.enums.Role;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateEmployee(
      String firstName,
      String middleName,
      String lastName,
      @Email
      String email,
      String phoneNumber,
      Gender gender,
      String password,
      LocalDate dateOfBirth,
      String nationalId,
      Role role,
      EmployeeStatus status,
      LocalDate hireDate,
      LocalDate confirmationDate,
      LocalDate exitDate,
      UUID departmentId,
      UUID positionId,
      UUID supervisorId
) {
}