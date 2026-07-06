package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.RegisterRequest;
import com.riverbank.employee_management_backend.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  @Override
  public Employee register(RegisterRequest registerRequest) {
    return null;
  }
}
