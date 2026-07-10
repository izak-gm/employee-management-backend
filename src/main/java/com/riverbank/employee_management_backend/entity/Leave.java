package com.riverbank.employee_management_backend.entity;

import com.riverbank.employee_management_backend.enums.LeaveStatus;
import com.riverbank.employee_management_backend.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee_leave")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Leave {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private Employee employee;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cover_employee_id")
  private Employee coverEmployee;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LeaveType leaveType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LeaveStatus status;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  private String reason;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "approved_by")
  private Employee approvedBy;

  private Instant createdAt;
  private Instant updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
    if (status == null) status = LeaveStatus.PENDING_COVER;
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }
}