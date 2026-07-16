package com.riverbank.employee_management_backend.dto.auth;

import com.riverbank.employee_management_backend.enums.EmployeeStatus;
import com.riverbank.employee_management_backend.enums.Role;
import com.riverbank.employee_management_backend.enus.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeResponse(

      UUID id,
      String employeeNumber,

      String firstName,
      String middleName,
      String lastName,

      String email,
      String phoneNumber,

      Gender gender,
      LocalDate dateOfBirth,
      String nationalId,

      Role role,
      EmployeeStatus status,

      LocalDate hireDate,
      LocalDate confirmationDate,
      LocalDate exitDate,

      UUID departmentId,
      String departmentName,

      UUID positionId,
      String positionName,

      UUID supervisorId,
      String supervisorName,

      LocalDateTime createdAt,
      LocalDateTime updatedAt

) {
}