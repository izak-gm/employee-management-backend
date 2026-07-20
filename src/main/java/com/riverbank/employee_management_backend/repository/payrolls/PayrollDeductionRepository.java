package com.riverbank.employee_management_backend.repository.payrolls;

import com.riverbank.employee_management_backend.entity.payrolls.PayrollDeduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PayrollDeductionRepository
      extends JpaRepository<PayrollDeduction, UUID> {
}