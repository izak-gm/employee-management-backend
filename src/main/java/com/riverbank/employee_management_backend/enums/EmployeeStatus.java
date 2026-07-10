package com.riverbank.employee_management_backend.enums;

public enum EmployeeStatus {
  INVITED,    // account created, email sent, password not yet set
  ACTIVE,     // password set, can log in
  INACTIVE    // deactivated by admin
}