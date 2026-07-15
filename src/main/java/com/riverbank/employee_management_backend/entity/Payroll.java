package com.riverbank.employee_management_backend.entity;

import com.riverbank.employee_management_backend.enums.PayrollStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
      name = "payroll",
      uniqueConstraints = {
            @UniqueConstraint(
                  columnNames = {
                        "employee_id",
                        "payrollMonth",
                        "payrollYear"
                  }
            )
      }
)
public class Payroll {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "employee_id")
  private Employee employee;

  @Column(nullable = false)
  private Integer payrollMonth;

  @Column(nullable = false)
  private Integer payrollYear;

  @Column(nullable = false)
  private LocalDate payrollDate;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal grossPay;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal taxablePay;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal totalEarnings;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal totalDeductions;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal netPay;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal paye;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal shif;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal nssf;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal housingLevy;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal employerNssf;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal employerShif;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PayrollStatus status;

  private String generatedBy;

  private String reversedBy;

  private String reversalReason;

  private LocalDateTime reversedAt;

  @CreationTimestamp
  private LocalDateTime generatedAt;

  @OneToMany(
        mappedBy = "payroll",
        cascade = CascadeType.ALL,
        orphanRemoval = true
  )
  private List<PayrollEarning> earnings;

  @OneToMany(
        mappedBy = "payroll",
        cascade = CascadeType.ALL,
        orphanRemoval = true
  )
  private List<PayrollDeduction> deductions;
}