package com.riverbank.employee_management_backend.dto.auth;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class AuthResponse {
  private String token;
}
