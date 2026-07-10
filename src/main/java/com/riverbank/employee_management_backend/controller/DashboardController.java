package com.riverbank.employee_management_backend.controller;

import com.riverbank.employee_management_backend.dto.DashboardStatsResponse;
import com.riverbank.employee_management_backend.service.employee.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Dashboard")
public class DashboardController {
  private final EmployeeService employeeService;

  @GetMapping("/dashboard/stats")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
    return ResponseEntity.ok(employeeService.getDashboardStats());
  }
}
