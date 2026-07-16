package com.riverbank.employee_management_backend.exception;

public class DepartmentNotFoundException extends RuntimeException {

  public DepartmentNotFoundException(String message) {
    super(message);
  }
}