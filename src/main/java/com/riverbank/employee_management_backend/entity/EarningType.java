package com.riverbank.employee_management_backend.entity;

import com.riverbank.employee_management_backend.enums.EarningCalculationType;
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
@Table(name = "earning_type")
public class EarningType {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private boolean taxable;

  @Column(nullable = false)
  private boolean active;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EarningCalculationType calculationType;

  @Column(precision = 19, scale = 2)
  private BigDecimal defaultAmount;

  private String description;

  @OneToMany(mappedBy = "earningType")
  private List<PayrollEarning> payrollEarnings;
}