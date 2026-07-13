package com.riverbank.employee_management_backend.dto.auth;

import com.riverbank.employee_management_backend.enums.Role;
import com.riverbank.employee_management_backend.enus.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEmployeeRequest(
      @NotBlank String firstName,
      @NotBlank String lastName,
      @Email @NotBlank String email,
      @NotBlank String phoneNumber,
      @NotNull Role role,
      @NotNull Gender gender

) {
}
