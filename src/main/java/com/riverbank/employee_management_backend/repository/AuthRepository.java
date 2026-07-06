package com.riverbank.employee_management_backend.repository;

import com.riverbank.employee_management_backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<Employee, UUID> {
  @Override
  Optional<Employee> findById(UUID uuid);

  Optional<Employee> findByEmail(String email);
}
