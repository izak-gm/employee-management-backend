package com.riverbank.employee_management_backend.dto;

import com.riverbank.employee_management_backend.enums.LeaveStatus;
import jakarta.validation.constraints.NotNull;

public record LeaveActionRequest(
      @NotNull LeaveStatus status // APPROVED or REJECTED

) {

}
