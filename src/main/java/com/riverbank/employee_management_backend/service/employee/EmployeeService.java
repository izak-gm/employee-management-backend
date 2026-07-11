package com.riverbank.employee_management_backend.service.employee;

import com.riverbank.employee_management_backend.dto.auth.EmployeeResponse;
import com.riverbank.employee_management_backend.dto.dashboard.DashboardStatsResponse;
import com.riverbank.employee_management_backend.dto.employee.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
  LeaveResponse applyForLeave(String username, @Valid LeaveRequest request);

  LeaveResponse actionLeave(UUID leaveId, @Valid LeaveActionRequest request, String username);

  List<LeaveResponse> getMyLeaves(String username);

  List<LeaveResponse> getAllLeaves();

  List<LeaveResponse> getPendingLeaves();

  List<EmployeeResponse> getActiveEmployees();

  DashboardStatsResponse getDashboardStats();

  LeaveResponse updateLeave(UUID leaveId, String username, @Valid LeaveRequest request);

  void deleteLeave(UUID leaveId, String username);

  LeaveResponse actionAsCover(UUID leaveId, @Valid CoverActionRequest request, String username);

  List<LeaveResponse> getPendingCoverActionsForMe(String username);

  List<LeaveBalanceResponse> getMyLeaveBalances(String username);

  LeaveResponse withdrawLeave(UUID leaveId, String username);

}
