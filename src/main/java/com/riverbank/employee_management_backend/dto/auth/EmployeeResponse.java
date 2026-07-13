package com.riverbank.employee_management_backend.dto.auth;

import com.riverbank.employee_management_backend.enums.Role;

import java.util.UUID;

public record EmployeeResponse(
      UUID id,
      String firstName,
      String lastName,
      String email,
      String phoneNumber,
      Role role,
      com.riverbank.employee_management_backend.enus.Gender gender) {
}
