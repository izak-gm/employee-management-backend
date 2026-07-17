package com.riverbank.employee_management_backend.exception;

public class UserAlreadyExistsException extends ResourceAlreadyExistsException {
  public UserAlreadyExistsException(String message) {
    super(message);
  }
}