package com.riverbank.employee_management_backend.controller;

import com.riverbank.employee_management_backend.dto.*;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.service.auth.AuthService;
import com.riverbank.employee_management_backend.service.employee.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {
  private final AuthService authService;
  private final EmployeeService employeeService;

  @PostMapping("/employees/create")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<EmployeeResponse> createEmployee(
        @Valid @RequestBody CreateEmployeeRequest request) {
    return ResponseEntity.ok(authService.createEmployee(request));
  }

  @PostMapping("/auth/login")
  @SecurityRequirements
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(authService.login(loginRequest));
  }

  @GetMapping("/employees/me")
  public EmployeeResponse getMyProfile(@AuthenticationPrincipal UserDetails currentUser) {
    return authService.getEmployeeByEmail(currentUser.getUsername());
  }

  @PutMapping("/employees/update-profile/me")
  public ResponseEntity<Employee> updateMyProfile(
        @RequestBody UpdateEmployee updateEmployee,
        @AuthenticationPrincipal UserDetails currentUser) {
    return ResponseEntity.ok(authService.updateOwnProfile(currentUser.getUsername(), updateEmployee));
  }

  // CHANGED: only Admin/SuperAdmin can look up another employee by ID
  @GetMapping("/employees/{employeeId}")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public EmployeeResponse getEmployeeById(@PathVariable("employeeId") UUID id,
                                          HttpServletRequest request) {
    return authService.getEmployeeById(id);
  }

  // CHANGED: only Admin/SuperAdmin can list/search employees
  @GetMapping("/employees")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public List<EmployeeResponse> getEmployeesByIds(
        @RequestParam(required = false) List<UUID> ids,
        @ModelAttribute EmployeeRequest employeeRequest
  ) {
    return authService.getEmployeesByIds(ids, employeeRequest);
  }

  // CHANGED: only Admin/SuperAdmin can edit another employee's profile by ID
  @PutMapping("/employees/update-profile/{employeeId}")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<Employee> updateEmployee(
        @RequestBody UpdateEmployee updateEmployee,
        @PathVariable("employeeId") UUID id
  ) {
    return ResponseEntity.ok(authService.updateProfile(id, updateEmployee));
  }

  @DeleteMapping("/employees/{employeeId}")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<MessageResponse> deleteEmployee(@PathVariable("employeeId") UUID id) {
    authService.deleteEmployee(id);
    return ResponseEntity.ok(new MessageResponse("Employee deleted successfully"));
  }

  //  password routes
  @PostMapping("/auth/setup-password")
  @SecurityRequirements
  public ResponseEntity<MessageResponse> setupPassword(
        @Valid @RequestBody SetPasswordRequest request) {
    authService.setPassword(request);
    return ResponseEntity.ok(new MessageResponse("Password set successfully. You can now log in."));
  }

// --- Forgot / reset password (public — token-gated) ---

  @PostMapping("/auth/forgot-password")
  @SecurityRequirements
  public ResponseEntity<MessageResponse> forgotPassword(
        @Valid @RequestBody ForgotPasswordRequest request) {
    authService.requestPasswordReset(request);
    return ResponseEntity.ok(new MessageResponse("If that email exists, a reset link has been sent."));
  }

  @PostMapping("/auth/reset-password")
  @SecurityRequirements
  public ResponseEntity<MessageResponse> resetPassword(
        @Valid @RequestBody ResetPasswordRequest request) {
    authService.resetPassword(request);
    return ResponseEntity.ok(new MessageResponse("Password reset successfully. You can now log in."));
  }

// --- Leave ---

  @PostMapping("/leaves")
  public ResponseEntity<LeaveResponse> applyForLeave(
        @Valid @RequestBody LeaveRequest request,
        @AuthenticationPrincipal UserDetails currentUser) {
    return ResponseEntity.ok(employeeService.applyForLeave(currentUser.getUsername(), request));
  }

  @GetMapping("/leaves/my")
  public ResponseEntity<List<LeaveResponse>> getMyLeaves(
        @AuthenticationPrincipal UserDetails currentUser) {
    return ResponseEntity.ok(employeeService.getMyLeaves(currentUser.getUsername()));
  }

  @GetMapping("/leaves")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<List<LeaveResponse>> getAllLeaves() {
    return ResponseEntity.ok(employeeService.getAllLeaves());
  }

  @GetMapping("/leaves/pending")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<List<LeaveResponse>> getPendingLeaves() {
    return ResponseEntity.ok(employeeService.getPendingLeaves());
  }

  @PutMapping("/leaves/{leaveId}/action")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<LeaveResponse> actionLeave(
        @PathVariable UUID leaveId,
        @Valid @RequestBody LeaveActionRequest request,
        @AuthenticationPrincipal UserDetails currentUser) {
    return ResponseEntity.ok(employeeService.actionLeave(leaveId, request, currentUser.getUsername()));
  }

// --- Active employees (for cover person selector) ---

  @GetMapping("/employees/active")
  public ResponseEntity<List<EmployeeResponse>> getActiveEmployees() {
    return ResponseEntity.ok(employeeService.getActiveEmployees());
  }

// --- Dashboard stats ---

  @GetMapping("/dashboard/stats")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
    return ResponseEntity.ok(employeeService.getDashboardStats());
  }
}