package com.riverbank.employee_management_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EmployeeManagementBackendApplication {

  static void main(String[] args) {
    SpringApplication.run(EmployeeManagementBackendApplication.class, args);
  }

}
