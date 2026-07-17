package com.riverbank.employee_management_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "department")
public class Department {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  private String description;

  private boolean active = true;

  @OneToMany(mappedBy = "department")
  private List<Employee> employees;
}