package com.riverbank.employee_management_backend.controller;

import com.riverbank.employee_management_backend.dto.*;
import com.riverbank.employee_management_backend.service.AuthService;
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

  //  public endpoint for registration
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterLoginRequest registerLoginRequest) {
    return ResponseEntity.ok(authService.register(registerLoginRequest));
  }

  //  Admin privilege to create a admin/superAdmin
  @PostMapping("/admins/register")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<AuthResponse> registerAdmins(
        @Valid @RequestBody AdminRegisterRequest request,
        @AuthenticationPrincipal UserDetails currentUser) {
    return ResponseEntity.ok(authService.registerAdmin(request, currentUser));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody RegisterLoginRequest registerLoginRequest) {
    return ResponseEntity.ok(authService.login(registerLoginRequest));
  }

  @GetMapping("/employees")
  public List<EmployeeResponse> getEmployeesByIds(
        @RequestParam(required = false) List<UUID> ids,
        @ModelAttribute EmployeeRequest employeeRequest
  ) {
    return authService.getEmployeesByIds(ids, employeeRequest);
  }

}