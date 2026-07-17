package com.riverbank.employee_management_backend.dto.auth;

import com.riverbank.employee_management_backend.enums.Gender;
import com.riverbank.employee_management_backend.enums.Role;
import com.riverbank.employee_management_backend.enums.payrolls.EmploymentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateEmployeeRequest(
      @NotBlank
      String firstName,
      String middleName,
      @NotBlank
      String lastName,
      @Email
      @NotBlank
      String email,
      @NotBlank
      String phoneNumber,
      @NotNull
      Gender gender,
      LocalDate dateOfBirth,
      String nationalId,
      @NotNull
      Role role,
      @NotNull
      LocalDate hireDate,
      LocalDate confirmationDate,
      EmploymentType employment_type,
      UUID departmentId,
      UUID positionId,
      UUID supervisorId
) {
}