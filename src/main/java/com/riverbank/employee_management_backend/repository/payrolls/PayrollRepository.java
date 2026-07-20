package com.riverbank.employee_management_backend.repository.payrolls;

import com.riverbank.employee_management_backend.entity.payrolls.Payroll;
import com.riverbank.employee_management_backend.enums.payrolls.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, UUID> {

    // Duplicate check before generating
    boolean existsByEmployee_IdAndPayrollMonthAndPayrollYear(
            UUID employeeId, int month, int year
    );

    // Fetch single with all relations to avoid N+1 when building response
    @Query("""
            SELECT p FROM Payroll p
            LEFT JOIN FETCH p.employee e
            LEFT JOIN FETCH e.department
            LEFT JOIN FETCH e.position
            LEFT JOIN FETCH p.generatedBy
            LEFT JOIN FETCH p.approvedBy
            LEFT JOIN FETCH p.reversedBy
            LEFT JOIN FETCH p.earnings ea
            LEFT JOIN FETCH ea.earningType
            LEFT JOIN FETCH p.deductions de
            LEFT JOIN FETCH de.deductionType
            WHERE p.id = :id
            """)
    Optional<Payroll> findByIdWithDetails(@Param("id") UUID id);

    // All payrolls for one employee
    List<Payroll> findByEmployee_IdOrderByPayrollYearDescPayrollMonthDesc(UUID employeeId);

    // All payrolls for a given month/year (admin view)
    @Query("""
            SELECT p FROM Payroll p
            LEFT JOIN FETCH p.employee e
            LEFT JOIN FETCH e.department
            WHERE p.payrollMonth = :month AND p.payrollYear = :year
            ORDER BY e.lastName ASC
            """)
    List<Payroll> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    // Filter by status
    List<Payroll> findByStatusOrderByPayrollYearDescPayrollMonthDesc(PayrollStatus status);

    // My payroll — employee sees their own
    Optional<Payroll> findByEmployee_IdAndPayrollMonthAndPayrollYear(
            UUID employeeId, int month, int year
    );
}
