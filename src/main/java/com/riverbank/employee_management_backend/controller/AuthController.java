package com.riverbank.employee_management_backend.controller;

import com.riverbank.employee_management_backend.dto.RegisterRequest;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<Employee> register(
        @RequestBody @Valid RegisterRequest registerRequest) {

    Employee employee = authService.register(registerRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(employee);
  }
}