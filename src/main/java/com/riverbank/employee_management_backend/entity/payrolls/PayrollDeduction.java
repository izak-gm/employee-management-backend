package com.riverbank.employee_management_backend.entity.payrolls;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
  @GeneratedValue(strategy = GenerationType.UUID)
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

  @CreationTimestamp
  private LocalDateTime createdAt;
}