package com.riverbank.employee_management_backend.service.employee;

import com.riverbank.employee_management_backend.dto.auth.EmployeeResponse;
import com.riverbank.employee_management_backend.dto.dashboard.DashboardStatsResponse;
import com.riverbank.employee_management_backend.dto.employee.LeaveActionRequest;
import com.riverbank.employee_management_backend.dto.employee.LeaveRequest;
import com.riverbank.employee_management_backend.dto.employee.LeaveResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
  LeaveResponse applyForLeave(String username, @Valid LeaveRequest request);

  List<LeaveResponse> getMyLeaves(String username);

  List<LeaveResponse> getAllLeaves();

  List<LeaveResponse> getPendingLeaves();

  LeaveResponse actionLeave(UUID leaveId, @Valid LeaveActionRequest request, String username);

  List<EmployeeResponse> getActiveEmployees();

  DashboardStatsResponse getDashboardStats();
}
