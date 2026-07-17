package com.riverbank.employee_management_backend.entity.payrolls;

import jakarta.persistence.*;
import lombok.*;

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
  private boolean fixed;

  private String description;

  @Builder.Default
  private boolean active = true;
}