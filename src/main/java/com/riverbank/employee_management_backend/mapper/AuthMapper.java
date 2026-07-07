package com.riverbank.employee_management_backend.mapper;

import com.riverbank.employee_management_backend.dto.AdminRegisterRequest;
import com.riverbank.employee_management_backend.dto.RegisterLoginRequest;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.enums.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthMapper {
  private final PasswordEncoder passwordEncoder;

  public Employee register(RegisterLoginRequest registerLoginRequest) {
    return Employee.builder()
          .email(registerLoginRequest.email())
          .role(Role.DEVELOPER)
          .password(passwordEncoder.encode(registerLoginRequest.password()))
          .build();
  }

  public Employee registerAdmin(AdminRegisterRequest registerRequest) {
    return Employee.builder()
          .email(registerRequest.email())
          .role(Role.DEVELOPER)
          .password(passwordEncoder.encode(registerRequest.password()))
          .build();
  }
}
