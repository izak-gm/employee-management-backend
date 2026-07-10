package com.riverbank.employee_management_backend.exception;

public class LeaveActionNotAllowedException extends RuntimeException {
  public LeaveActionNotAllowedException(String message) {
    super(message);
  }
}