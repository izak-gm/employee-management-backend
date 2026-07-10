package com.riverbank.employee_management_backend.dto.employee;

import com.riverbank.employee_management_backend.enums.LeaveType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record LeaveRequest(
      @NotNull LeaveType leaveType,
      @NotNull LocalDate startDate,
      @NotNull LocalDate endDate,
      String reason,
      UUID coverEmployeeId
) {
}