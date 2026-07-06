package com.riverbank.employee_management_backend.entity;

import com.riverbank.employee_management_backend.enums.Auth;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "employee")
public class Employee {
  @Id
  @GeneratedValue
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String password;

  @Enumerated(EnumType.STRING)
  private Auth auth;

}
