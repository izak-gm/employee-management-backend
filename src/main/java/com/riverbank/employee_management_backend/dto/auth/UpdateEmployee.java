package com.riverbank.employee_management_backend.dto.auth;

import com.riverbank.employee_management_backend.enums.Role;
import com.riverbank.employee_management_backend.enus.Gender;
import jakarta.validation.constraints.Email;

public record UpdateEmployee(
      String firstName,
      String lastName,
//      TODO: standardize the phone number
      String phoneNumber,
      @Email(message = "Email must be valid")
      String email,
      Role role,
      Gender gender
) {
}
