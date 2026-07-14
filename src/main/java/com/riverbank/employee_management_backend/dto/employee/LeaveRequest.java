package com.riverbank.employee_management_backend.dto.employee;

import com.riverbank.employee_management_backend.enums.LeaveType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record LeaveRequest(

      @NotNull(message = "Leave type is required")
      LeaveType leaveType,

      @NotNull(message = "Start date is required")
      LocalDate startDate,

      @NotNull(message = "End date is required")
      LocalDate endDate,

      String reason,

      @NotNull(message = "Please select a cover employee")
      UUID coverEmployeeId

) {
}