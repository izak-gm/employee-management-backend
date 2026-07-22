package com.riverbank.employee_management_backend.entity.payrolls;

import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.enums.payrolls.PayrollStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
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
            @UniqueConstraint(columnNames = {"employee_id", "payrollMonth", "payrollYear"})
      },
      indexes = {
            @Index(name = "idx_payroll_employee", columnList = "employee_id"),
            @Index(name = "idx_payroll_status", columnList = "status"),
            @Index(name = "idx_payroll_year_month", columnList = "payrollYear, payrollMonth")
      }
)
public class Payroll {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
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

  @Builder.Default
  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal totalEarnings = BigDecimal.ZERO;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal totalDeductions;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal netPay;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal paye;

  //  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal incomeTax;

  //  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal personalRelief;

  private BigDecimal statutoryDeductions;

  private BigDecimal payAfterStatutoryDeductions;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "generated_by")
  private Employee generatedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "approved_by")
  private Employee approvedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reversed_by")
  private Employee reversedBy;

  private LocalDateTime approvedAt;

  private LocalDate paymentDate;

  private String paymentReference;

  @Column(nullable = false, unique = true)
  private String payrollNumber;

  @Column(length = 500)
  private String remarks;

  private String reversalReason;

  private LocalDateTime reversedAt;

  @CreationTimestamp
  private LocalDateTime generatedAt;

  @OneToMany(
        mappedBy = "payroll",
        cascade = CascadeType.ALL,
        orphanRemoval = true
  )
  private Set<PayrollEarning> earnings;

  @OneToMany(
        mappedBy = "payroll",
        cascade = CascadeType.ALL,
        orphanRemoval = true
  )
  private Set<PayrollDeduction> deductions;
}