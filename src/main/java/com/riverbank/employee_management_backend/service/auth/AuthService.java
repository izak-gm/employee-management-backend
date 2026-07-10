package com.riverbank.employee_management_backend.service.auth;

import com.riverbank.employee_management_backend.dto.*;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.enums.Role;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

public interface AuthService {

  AuthResponse login(LoginRequest loginRequest);

  EmployeeResponse createEmployee(@Valid CreateEmployeeRequest request);

  void setPassword(@Valid SetPasswordRequest request);

  void requestPasswordReset(@Valid ForgotPasswordRequest request);

  void resetPassword(@Valid ResetPasswordRequest request);


  void validateRoleAssignment(UserDetails currentUser, Role role);

  List<EmployeeResponse> getEmployeesByIds(List<UUID> ids, EmployeeRequest employeeRequest);

  EmployeeResponse getEmployeeById(UUID id);

  Employee updateProfile(UUID id, UpdateEmployee updateEmployee);

  void deleteEmployee(UUID id);

  Employee updateOwnProfile(String username, UpdateEmployee updateEmployee);

  EmployeeResponse getEmployeeByEmail(String username);
}
