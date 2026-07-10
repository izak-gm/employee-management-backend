package com.riverbank.employee_management_backend.dto.dashboard;

public record DashboardStatsResponse(
      long totalEmployees,
      long activeEmployees,
      long invitedEmployees,
      long inactiveEmployees,
      long totalAdmins,
      long totalSuperAdmins,
      long pendingLeaves,
      long approvedLeaves,
      long rejectedLeaves,
      long totalLeaves
) {
}