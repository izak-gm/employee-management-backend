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
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  //  public endpoint for registration
  @PostMapping("/register")
  @SecurityRequirements
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterLoginRequest registerLoginRequest) {
    return ResponseEntity.ok(authService.register(registerLoginRequest));
  }

  //  Admin privilege to create a admin/superAdmin
  @PostMapping("/admin/register")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<AuthResponse> registerAdmins(
        @Valid @RequestBody AdminRegisterRequest request,
        @AuthenticationPrincipal UserDetails currentUser) {
    return ResponseEntity.ok(authService.registerAdmin(request, currentUser));
  }

  //  public endpoint for login
  @PostMapping("/login")
  @SecurityRequirements
  public ResponseEntity<AuthResponse> login(@RequestBody RegisterLoginRequest registerLoginRequest) {
    return ResponseEntity.ok(authService.login(registerLoginRequest));
  }

  @GetMapping("/employees/{employeeId}")
  public EmployeeResponse getEmployeeById(@PathVariable("employeeId") UUID id,
                                          HttpServletRequest request) {
    return authService.getEmployeeById(id);
  }

  @GetMapping("/employees/all")
  public List<EmployeeResponse> getEmployeesByIds(
        @RequestParam(required = false) List<UUID> ids,
        @ModelAttribute EmployeeRequest employeeRequest
  ) {
    return authService.getEmployeesByIds(ids, employeeRequest);
  }

  @PutMapping("/employee/update-profile/{employeeId}")
  public ResponseEntity<Employee> updateEmployee(
        @RequestBody UpdateEmployee updateEmployee,
        @PathVariable("employeeId") UUID id
  ) {
    return ResponseEntity.ok(authService.updateProfile(id, updateEmployee));
  }

  @DeleteMapping("/employee/{employeeId}")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
    authService.deleteEmployee(id);
    return ResponseEntity.noContent().build();
  }
}