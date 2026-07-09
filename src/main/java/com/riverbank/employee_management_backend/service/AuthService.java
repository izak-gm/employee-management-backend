package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.*;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.enums.Role;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

public interface AuthService {
  AuthResponse register(RegisterLoginRequest registerLoginRequest);

  AuthResponse registerAdmin(AdminRegisterRequest request, UserDetails currentUser);

  AuthResponse login(RegisterLoginRequest registerLoginRequest);

  void validateRoleAssignment(UserDetails currentUser, Role role);

  List<EmployeeResponse> getEmployeesByIds(List<UUID> ids, EmployeeRequest employeeRequest);

  EmployeeResponse getEmployeeById(UUID id);

  Employee updateProfile(UUID id, UpdateEmployee updateEmployee);

  void deleteEmployee(UUID id);

  Employee updateOwnProfile(String username, UpdateEmployee updateEmployee);

  EmployeeResponse getEmployeeByEmail(String username);
}
