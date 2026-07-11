package com.riverbank.employee_management_backend.dto.employee;

import jakarta.validation.constraints.NotNull;

public record CoverActionRequest(
      @NotNull Boolean accept   // true = accept, false = decline
) {
}
