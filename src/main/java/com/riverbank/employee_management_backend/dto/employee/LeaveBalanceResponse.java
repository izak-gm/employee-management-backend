package com.riverbank.employee_management_backend.dto.employee;

import com.riverbank.employee_management_backend.enums.LeaveType;

public record LeaveBalanceResponse(LeaveType leaveType, int maxDays, int usedDays, int remainingDays,
                                   boolean unlimited) {
}