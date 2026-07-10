package com.riverbank.employee_management_backend.controller;

import com.riverbank.employee_management_backend.dto.*;
import com.riverbank.employee_management_backend.dto.auth.*;
import com.riverbank.employee_management_backend.dto.employee.*;

import com.riverbank.employee_management_backend.service.employee.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Employee Properties")
public class EmployeeController {
  private final EmployeeService employeeService;

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

  @GetMapping("/leaves/notifications")
  public ResponseEntity<List<LeaveResponse>> myNotifications(@AuthenticationPrincipal UserDetails user) {
    return ResponseEntity.ok(employeeService.getPendingCoverActionsForMe(user.getUsername()));
  }
// --- Active employees (for cover person selector) ---

  @GetMapping("/employees/active")
  public ResponseEntity<List<EmployeeResponse>> getActiveEmployees() {
    return ResponseEntity.ok(employeeService.getActiveEmployees());
  }

  @GetMapping("/leaves/balance")
  public ResponseEntity<List<LeaveBalanceResponse>> myBalance(@AuthenticationPrincipal UserDetails user) {
    return ResponseEntity.ok(employeeService.getMyLeaveBalances(user.getUsername()));
  }

  @PutMapping("/leaves/{leaveId}/action")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<LeaveResponse> actionLeave(
        @PathVariable UUID leaveId,
        @Valid @RequestBody LeaveActionRequest request,
        @AuthenticationPrincipal UserDetails currentUser) {
    return ResponseEntity.ok(employeeService.actionLeave(leaveId, request, currentUser.getUsername()));
  }

  @PutMapping("/leaves/{leaveId}")
  public ResponseEntity<LeaveResponse> updateLeave(@PathVariable UUID leaveId,
                                                   @Valid @RequestBody LeaveRequest request, @AuthenticationPrincipal UserDetails user) {
    return ResponseEntity.ok(employeeService.updateLeave(leaveId, user.getUsername(), request));
  }

  @PutMapping("/leaves/{leaveId}/cover-action")
  public ResponseEntity<LeaveResponse> coverAction(@PathVariable UUID leaveId,
                                                   @Valid @RequestBody CoverActionRequest request, @AuthenticationPrincipal UserDetails user) {
    return ResponseEntity.ok(employeeService.actionAsCover(leaveId, request, user.getUsername()));
  }

  @DeleteMapping("/leaves/{leaveId}")
  public ResponseEntity<MessageResponse> deleteLeave(@PathVariable UUID leaveId,
                                                     @AuthenticationPrincipal UserDetails user) {
    employeeService.deleteLeave(leaveId, user.getUsername());
    return ResponseEntity.ok(new MessageResponse("Leave deleted"));
  }


  @DeleteMapping("/leaves/{leaveId}/withdraw")
  public ResponseEntity<LeaveResponse> withdrawLeave(@PathVariable UUID leaveId, @AuthenticationPrincipal UserDetails user) {
    return ResponseEntity.ok(employeeService.withdrawLeave(leaveId, user.getUsername()));
  }
}