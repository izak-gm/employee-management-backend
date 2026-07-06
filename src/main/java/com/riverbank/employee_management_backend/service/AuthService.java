package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.RegisterRequest;
import com.riverbank.employee_management_backend.entity.Employee;
import jakarta.validation.Valid;

public interface AuthService {
  Employee register(@Valid RegisterRequest registerRequest);
}
