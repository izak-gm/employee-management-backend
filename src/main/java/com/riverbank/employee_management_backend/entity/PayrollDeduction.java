package com.riverbank.employee_management_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payroll_deduction")
public class PayrollDeduction {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "payroll_id")
  private Payroll payroll;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "deduction_type_id")
  private DeductionType deductionType;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  private String remarks;
}