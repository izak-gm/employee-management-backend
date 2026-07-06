package com.riverbank.employee_management_backend.dto;

public record UpdateEmployee(
      String firstName,
      String lastName,
      String phoneNumber,
      String email,
      String password
) {
}
