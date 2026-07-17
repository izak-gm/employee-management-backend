package com.riverbank.employee_management_backend.mapper;

import com.riverbank.employee_management_backend.dto.auth.CreateEmployeeRequest;
import com.riverbank.employee_management_backend.dto.auth.EmployeeResponse;
import com.riverbank.employee_management_backend.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

  /**
   * Maps a CreateEmployeeRequest to an Employee entity.
   * Fields requiring business logic (department, employee number, etc.)
   * are assigned in the service layer.
   */
  public Employee toEmployee(CreateEmployeeRequest request) {
    return Employee.builder()
          .firstName(request.firstName())
          .middleName(request.middleName())
          .lastName(request.lastName())
          .email(request.email())
          .phoneNumber(request.phoneNumber())
          .gender(request.gender())
          .dateOfBirth(request.dateOfBirth())
          .nationalId(request.nationalId())
          .role(request.role())
          .hireDate(request.hireDate())
          .employmentType(request.employment_type())
          .confirmationDate(request.confirmationDate())
          .password("")
          .build();
  }

  /**
   * Maps an Employee entity to EmployeeResponse.
   */
  public EmployeeResponse toEmployeeResponse(Employee employee) {
    return new EmployeeResponse(
          employee.getId(),
          employee.getEmployeeNumber(),

          employee.getFirstName(),
          employee.getMiddleName(),
          employee.getLastName(),

          employee.getEmail(),
          employee.getPhoneNumber(),

          employee.getGender(),
          employee.getDateOfBirth(),
          employee.getNationalId(),

          employee.getRole(),
          employee.getStatus(),

          employee.getHireDate(),
          employee.getConfirmationDate(),
          employee.getExitDate(),

          employee.getDepartment() != null
                ? employee.getDepartment().getId()
                : null,

          employee.getDepartment() != null
                ? employee.getDepartment().getName()
                : null,

          employee.getPosition() != null
                ? employee.getPosition().getId()
                : null,

          employee.getPosition() != null
                ? employee.getPosition().getName()
                : null,

          employee.getSupervisor() != null
                ? employee.getSupervisor().getId()
                : null,

          employee.getSupervisor() != null
                ? employee.getSupervisor().getFirstName() + " "
                  + employee.getSupervisor().getLastName()
                : null,

          employee.getCreatedAt(),
          employee.getUpdatedAt()
    );
  }

  /**
   * Updates an existing employee from a request.
   * Business-related fields are intentionally excluded.
   */
  public void updateEmployee(Employee employee, CreateEmployeeRequest request) {
    employee.setFirstName(request.firstName());
    employee.setMiddleName(request.middleName());
    employee.setLastName(request.lastName());
    employee.setEmail(request.email());
    employee.setPhoneNumber(request.phoneNumber());
    employee.setGender(request.gender());
    employee.setDateOfBirth(request.dateOfBirth());
    employee.setNationalId(request.nationalId());
    employee.setRole(request.role());
    employee.setHireDate(request.hireDate());
    employee.setConfirmationDate(request.confirmationDate());
  }
}