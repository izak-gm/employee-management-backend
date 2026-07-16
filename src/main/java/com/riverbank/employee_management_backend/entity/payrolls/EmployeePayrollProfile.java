package com.riverbank.employee_management_backend.entity.payrolls;

import com.riverbank.employee_management_backend.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employee_payroll_profile")
public class EmployeePayrollProfile {

  @Id
  @GeneratedValue
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false, unique = true)
  private Employee employee;

  @Column(nullable = false, precision = 18, scale = 2)
  private BigDecimal basicSalary;

  @Builder.Default
  @Column(precision = 18, scale = 2)
  private BigDecimal houseAllowance = BigDecimal.ZERO;

  @Builder.Default
  @Column(precision = 18, scale = 2)
  private BigDecimal transportAllowance = BigDecimal.ZERO;

  @Builder.Default
  @Column(precision = 18, scale = 2)
  private BigDecimal medicalAllowance = BigDecimal.ZERO;

  @Builder.Default
  @Column(precision = 18, scale = 2)
  private BigDecimal otherAllowance = BigDecimal.ZERO;

  @Column(nullable = false)
  private String bankName;

  private String bankBranch;

  @Column(nullable = false)
  private String accountNumber;

  @Column(nullable = false)
  private String kraPin;

  @Column(nullable = false)
  private String shifNumber;

  @Column(nullable = false)
  private String nssfNumber;

  @Builder.Default
  @Column(precision = 18, scale = 2)
  private BigDecimal pensionContribution = BigDecimal.ZERO;

  private LocalDate effectiveFrom;

  private LocalDate effectiveTo;

  @Builder.Default
  private boolean active = true;
}