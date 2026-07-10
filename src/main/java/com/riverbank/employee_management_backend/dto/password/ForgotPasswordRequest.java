package com.riverbank.employee_management_backend.dto.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
      @Email @NotBlank String email

) {
}
