package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.AuthResponse;
import com.riverbank.employee_management_backend.dto.RegisterRequest;
import jakarta.validation.Valid;

public interface AuthService {
  AuthResponse register(@Valid RegisterRequest registerRequest);
}
