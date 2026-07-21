package com.riverbank.employee_management_backend.repository.payrolls;

import com.riverbank.employee_management_backend.entity.payrolls.PayrollDeduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollDeductionRepository extends JpaRepository<PayrollDeduction, UUID> {

  @Query("""
        SELECT d
        FROM PayrollDeduction d
        LEFT JOIN FETCH d.deductionType
        WHERE d.payroll.id = :payrollId
        ORDER BY d.id
        """)
  List<PayrollDeduction> findByPayrollId(@Param("payrollId") UUID payrollId);
}