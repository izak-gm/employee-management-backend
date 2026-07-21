package com.riverbank.employee_management_backend.repository.payrolls;

import com.riverbank.employee_management_backend.entity.payrolls.PayrollEarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollEarningRepository extends JpaRepository<PayrollEarning, UUID> {

  @Query("""
        SELECT e
        FROM PayrollEarning e
        LEFT JOIN FETCH e.earningType
        WHERE e.payroll.id = :payrollId
        ORDER BY e.id
        """)
  List<PayrollEarning> findByPayrollId(@Param("payrollId") UUID payrollId);
}