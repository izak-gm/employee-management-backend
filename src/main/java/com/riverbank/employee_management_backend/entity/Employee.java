package com.riverbank.employee_management_backend.entity;

import com.riverbank.employee_management_backend.entity.payrolls.EmployeePayrollProfile;
import com.riverbank.employee_management_backend.entity.payrolls.Payroll;
import com.riverbank.employee_management_backend.enums.EmployeeStatus;
import com.riverbank.employee_management_backend.enums.Gender;
import com.riverbank.employee_management_backend.enums.Role;
import com.riverbank.employee_management_backend.enums.payrolls.EmploymentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employee")
public class Employee implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String employeeNumber;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  private String middleName;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Gender gender;

  private LocalDate dateOfBirth;

  @Column(unique = true)
  private String nationalId;

  private String profilePhoto;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EmployeeStatus status;

  private LocalDate hireDate;

  private LocalDate confirmationDate;

  private LocalDate exitDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EmploymentType employmentType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id")
  private Department department;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "position_id")
  private Position position;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "supervisor_id")
  private Employee supervisor;

  @OneToMany(mappedBy = "supervisor")
  private List<Employee> subordinates;

  @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private InviteToken inviteToken;

  @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
  private List<Leave> leaves;

  @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
  private List<Payroll> payrolls;

  @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private EmployeePayrollProfile payrollProfile;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return status == EmployeeStatus.ACTIVE;
  }
}