package com.riverbank.employee_management_backend.repository.payrolls;

import com.riverbank.employee_management_backend.entity.payrolls.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, UUID> {

  Optional<Payroll> findByEmployeeIdAndPayrollMonthAndPayrollYear(
        UUID employeeId,
        Integer payrollMonth,
        Integer payrollYear
  );

  List<Payroll> findByEmployeeId(UUID employeeId);

  List<Payroll> findByPayrollMonthAndPayrollYear(
        Integer month,
        Integer year
  );
}