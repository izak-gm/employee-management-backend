package com.riverbank.employee_management_backend.controller;

import com.riverbank.employee_management_backend.dto.AdminRegisterRequest;
import com.riverbank.employee_management_backend.dto.AuthResponse;
import com.riverbank.employee_management_backend.dto.RegisterRequest;
import com.riverbank.employee_management_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

  private final AuthService authService;

  //  public endpoint for registration
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
    return ResponseEntity.ok(authService.register(registerRequest));
  }

  //  Admin privilege to create a admin/superAdmin
  @PostMapping("/admins/register")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public ResponseEntity<AuthResponse> registerAdmins(
        @Valid @RequestBody AdminRegisterRequest request,
        @AuthenticationPrincipal UserDetails currentUser) {
    return ResponseEntity.ok(authService.registerAdmin(request, currentUser));
  }
  

}