package com.riverbank.employee_management_backend.repository;

import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.enums.EmployeeStatus;
import com.riverbank.employee_management_backend.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
  @Override
  Optional<Employee> findById(UUID uuid);

  Optional<Employee> findByEmail(String email);

  boolean existsByEmail(String email);

  List<Employee> findByStatus(EmployeeStatus status);

  long countByStatus(EmployeeStatus status);

  long countByRole(Role role);
}
