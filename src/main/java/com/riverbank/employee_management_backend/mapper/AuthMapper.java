package com.riverbank.employee_management_backend.mapper;

import com.riverbank.employee_management_backend.dto.RegisterRequest;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.enums.Auth;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthMapper {
  private final PasswordEncoder passwordEncoder;

  public Employee register(RegisterRequest registerRequest) {
    return Employee.builder()
          .email(registerRequest.email())
          .auth(Auth.DEVELOPER)
          .password(passwordEncoder.encode(registerRequest.password()))
          .build();
  }
}
