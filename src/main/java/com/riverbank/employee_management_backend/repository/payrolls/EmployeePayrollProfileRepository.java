package com.riverbank.employee_management_backend.repository.payrolls;

import com.riverbank.employee_management_backend.entity.payrolls.EmployeePayrollProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeePayrollProfileRepository extends JpaRepository<EmployeePayrollProfile, UUID> {
  Optional<EmployeePayrollProfile> findByKraPin(String kraPin);

  Optional<EmployeePayrollProfile> findByAccountNumber(String accountNumber);

  Optional<EmployeePayrollProfile> findByShifNumber(String shifNumber);

  Optional<EmployeePayrollProfile> findByNssfNumber(String nssfNumber);

  Optional<EmployeePayrollProfile> findByEmployeeIdAndActiveTrue(UUID employeeId);

  boolean existsByEmployeeId(UUID employeeId);
}
