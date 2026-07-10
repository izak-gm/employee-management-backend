package com.riverbank.employee_management_backend.dto;

import jakarta.validation.constraints.Email;

public record UpdateEmployee(
      String firstName,
      String lastName,
//      TODO: standardize the phone number
      String phoneNumber,
      @Email(message = "Email must be valid")
      String email
) {
}
