package com.riverbank.employee_management_backend.entity;

import com.riverbank.employee_management_backend.enums.DeductionCalculationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "deduction_type")
public class DeductionType {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private boolean statutory;

  @Column(nullable = false)
  private boolean taxable;

  @Column(nullable = false)
  private boolean active;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DeductionCalculationType calculationType;

  @Column(precision = 19, scale = 2)
  private BigDecimal fixedAmount;

  @Column(precision = 5, scale = 2)
  private BigDecimal percentage;

  private String description;

  @OneToMany(mappedBy = "deductionType")
  private List<PayrollDeduction> payrollDeductions;
}