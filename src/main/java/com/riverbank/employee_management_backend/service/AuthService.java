package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.AdminRegisterRequest;
import com.riverbank.employee_management_backend.dto.AuthResponse;
import com.riverbank.employee_management_backend.dto.RegisterRequest;
import com.riverbank.employee_management_backend.enums.Auth;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
  AuthResponse register(RegisterRequest registerRequest);

  AuthResponse registerAdmin(AdminRegisterRequest request, UserDetails currentUser);

  void validateRoleAssignment(UserDetails currentUser, Auth auth);
}
