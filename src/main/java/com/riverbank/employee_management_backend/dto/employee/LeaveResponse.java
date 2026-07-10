package com.riverbank.employee_management_backend.dto.employee;

import com.riverbank.employee_management_backend.enums.LeaveStatus;
import com.riverbank.employee_management_backend.enums.LeaveType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record LeaveResponse(
      UUID id,
      UUID employeeId,
      String employeeFullName,
      UUID coverEmployeeId,
      String coverEmployeeFullName,
      LeaveType leaveType,
      LeaveStatus status,
      LocalDate startDate,
      LocalDate endDate,
      String reason,
      String approvedByFullName,
      Instant createdAt
) {
}