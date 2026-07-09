package com.riverbank.employee_management_backend.controller;

import com.riverbank.employee_management_backend.dto.*;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.service.AuthService;
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

  // public endpoint for registration
  @PostMapping("/auth/register")
  @SecurityRequirements
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterLoginRequest registerLoginRequest) {
    return ResponseEntity.ok(authService.register(registerLoginRequest));
  }

  // Admin privilege to create an admin/superAdmin
  @PostMapping("/auth/admin/register")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<AuthResponse> registerAdmins(
        @Valid @RequestBody AdminRegisterRequest request,
        @AuthenticationPrincipal UserDetails currentUser) {
    return ResponseEntity.ok(authService.registerAdmin(request, currentUser));
  }

  // public endpoint for login
  @PostMapping("/auth/login")
  @SecurityRequirements
  public ResponseEntity<AuthResponse> login(@RequestBody RegisterLoginRequest registerLoginRequest) {
    return ResponseEntity.ok(authService.login(registerLoginRequest));
  }

  // NEW: current user views their own profile
  @GetMapping("/employees/me")
  public EmployeeResponse getMyProfile(@AuthenticationPrincipal UserDetails currentUser) {
    return authService.getEmployeeByEmail(currentUser.getUsername());
  }

  // NEW: current user updates their own profile
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
}