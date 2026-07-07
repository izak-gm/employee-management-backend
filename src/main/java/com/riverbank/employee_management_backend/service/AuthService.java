package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.*;
import com.riverbank.employee_management_backend.enums.Role;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

public interface AuthService {
  AuthResponse register(RegisterLoginRequest registerLoginRequest);

  AuthResponse registerAdmin(AdminRegisterRequest request, UserDetails currentUser);

  void validateRoleAssignment(UserDetails currentUser, Role role);

  List<EmployeeResponse> getEmployeesByIds(List<UUID> ids, EmployeeRequest employeeRequest);

  AuthResponse login(RegisterLoginRequest registerLoginRequest);
}
