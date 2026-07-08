package com.riverbank.employee_management_backend.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse(
      Instant timestamp,
      int status,
      String error,
      String message,
      String path
) {
}