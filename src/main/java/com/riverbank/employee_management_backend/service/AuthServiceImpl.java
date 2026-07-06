package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.AuthResponse;
import com.riverbank.employee_management_backend.dto.RegisterRequest;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.mapper.AuthMapper;
import com.riverbank.employee_management_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final EmployeeRepository repository;
  private final AuthMapper authMapper;
  private final JwtService jwtService;

  @Override
  public AuthResponse register(RegisterRequest registerRequest) {
    Employee employee = repository.save(authMapper.register(registerRequest));
    var jwtToken = jwtService.generateToken(employee);
    return AuthResponse.builder()
          .token(jwtToken)
          .build();
  }
}
