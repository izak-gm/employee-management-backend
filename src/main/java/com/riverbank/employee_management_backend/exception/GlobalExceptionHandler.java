package com.riverbank.employee_management_backend.exception;

import com.riverbank.employee_management_backend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(EmployeeNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEmployeeNotFound(
        EmployeeNotFoundException ex, HttpServletRequest request) {
    log.warn("Employee not found: {}", ex.getMessage());
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserExists(
        UserAlreadyExistsException ex, HttpServletRequest request) {
    log.warn("Registration attempt with existing email on {}: {}", request.getRequestURI(), ex.getMessage());
    return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(
        BadCredentialsException ex, HttpServletRequest request) {
    log.warn("Failed login attempt on {}", request.getRequestURI());
    return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password", request);
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ErrorResponse> handleDisabledAccount(
        DisabledException ex, HttpServletRequest request) {
    log.warn("Disabled account attempted login on {}", request.getRequestURI());
    return buildResponse(HttpStatus.FORBIDDEN, "This account has been disabled", request);
  }

  @ExceptionHandler(LockedException.class)
  public ResponseEntity<ErrorResponse> handleLockedAccount(
        LockedException ex, HttpServletRequest request) {
    log.warn("Locked account attempted login on {}", request.getRequestURI());
    return buildResponse(HttpStatus.FORBIDDEN, "This account has been locked", request);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUsernameNotFound(
        UsernameNotFoundException ex, HttpServletRequest request) {
    log.warn("Username not found on {}", request.getRequestURI());
    return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password", request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(
        AccessDeniedException ex, HttpServletRequest request) {
    log.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());
    return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(
        MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(fe ->
          fieldErrors.put(fe.getField(), fe.getDefaultMessage())
    );

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", Instant.now());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Validation Failed");
    body.put("path", request.getRequestURI());
    body.put("fieldErrors", fieldErrors);

    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(
        RuntimeException ex, HttpServletRequest request) {
    log.error("Unhandled exception on {}", request.getRequestURI(), ex);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
  }

  @ExceptionHandler(LeaveActionNotAllowedException.class)
  public ResponseEntity<ErrorResponse> handleLeaveActionNotAllowed(
        LeaveActionNotAllowedException ex, HttpServletRequest request) {
    ErrorResponse error = ErrorResponse.builder()
          .timestamp(Instant.now())
          .status(HttpStatus.CONFLICT.value())
          .error(HttpStatus.CONFLICT.getReasonPhrase())
          .message(ex.getMessage())
          .path(request.getRequestURI())
          .build();
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  private ResponseEntity<ErrorResponse> buildResponse(
        HttpStatus status, String message, HttpServletRequest request) {
    ErrorResponse error = ErrorResponse.builder()
          .timestamp(Instant.now())
          .status(status.value())
          .error(status.getReasonPhrase())
          .message(message)
          .path(request.getRequestURI())
          .build();
    return ResponseEntity.status(status).body(error);
  }
}