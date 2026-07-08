package com.riverbank.employee_management_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateEmployee(
      String firstName,
      String lastName,
//      TODO: standardize the phone number
      String phoneNumber,
      @Email(message = "Email must be valid")
      String email,
      @Size(min = 8, message = "Password must be at least 8 characters")
      String password
) {
}
