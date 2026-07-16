package com.riverbank.employee_management_backend.service.implementation;

import com.riverbank.employee_management_backend.dto.auth.*;
import com.riverbank.employee_management_backend.dto.password.ForgotPasswordRequest;
import com.riverbank.employee_management_backend.dto.password.ResetPasswordRequest;
import com.riverbank.employee_management_backend.dto.password.SetPasswordRequest;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.entity.InviteToken;
import com.riverbank.employee_management_backend.enums.EmployeeStatus;
import com.riverbank.employee_management_backend.enums.Role;
import com.riverbank.employee_management_backend.exception.EmployeeNotFoundException;
import com.riverbank.employee_management_backend.exception.UserAlreadyExistsException;
import com.riverbank.employee_management_backend.mapper.AuthMapper;
import com.riverbank.employee_management_backend.repository.*;
import com.riverbank.employee_management_backend.service.auth.AuthService;
import com.riverbank.employee_management_backend.service.email.EmailService;
import com.riverbank.employee_management_backend.service.jwt.JwtService;
import com.riverbank.employee_management_backend.util.EmployeeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.riverbank.employee_management_backend.util.StringUtils.safe;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

  private static final Map<Role, Set<Role>> Auth_HIERARCHY = Map.of(
        Role.SUPERADMIN, Set.of(Role.HR_ADMIN, Role.SUPERADMIN, Role.SOFTWARE_ENGINEER),
        Role.HR_ADMIN, Set.of(Role.SOFTWARE_ENGINEER)
  );

  private final EmployeeRepository employeeRepository;
  private final AuthMapper authMapper;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final InviteTokenRepository inviteTokenRepository;
  private final EmailService emailService;
  private final EmployeeUtils employeeUtils;
  private final DepartmentRepository departmentRepository;
  private final PositionRepository positionRepository;
  private final EmployeePayrollProfileRepository payrollProfileRepository;

  @Value("${app.invite-token-expiry-hours:48}")
  private int inviteTokenExpiryHours;
  @Value("${app.reset-token-expiry-hours:1}")
  private int resetTokenExpiryHours;

  @Override
  public void validateRoleAssignment(UserDetails currentUser, Role requestedRole) {
    Role callerRole = extractAuth(currentUser); // pull from authorities
    Set<Role> allowed = Auth_HIERARCHY.getOrDefault(callerRole, Set.of());

    if (!allowed.contains(requestedRole)) {
      throw new AccessDeniedException("Cannot assign Auth: " + requestedRole);
    }
  }

  private Role extractAuth(UserDetails currentUser) {
    return currentUser.getAuthorities().stream()
          .findFirst()
          .map(a -> Role.valueOf(a.getAuthority().replace("ROLE_", "")))
          .orElseThrow(() -> new AccessDeniedException("No role found"));
  }

  @Override
  public AuthResponse login(LoginRequest loginRequest) {
    authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.password()
          )
    );
    var employee = employeeRepository.findByEmail(loginRequest.email())
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    var jwtToken = jwtService.generateToken(employee);
    return AuthResponse.builder()
          .token(jwtToken)
          .build();
  }

  // --- Employee creation (Admin/SuperAdmin only) ---

  @Transactional
  public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
    if (employeeRepository.existsByEmail(request.email())) {
      throw new UserAlreadyExistsException("An employee with this email already exists");
    }

    Employee employee = Employee.builder()
          .firstName(request.firstName())
          .lastName(request.lastName())
          .email(request.email())
          .phoneNumber(request.phoneNumber())
          .role(request.role())
          .gender(request.gender())
          .status(EmployeeStatus.INVITED)
          .password("") // set later via invite link
          .build();

    employee = employeeRepository.save(employee);

    String token = UUID.randomUUID().toString();
    InviteToken inviteToken = InviteToken.builder()
          .token(token)
          .employee(employee)
          .expiresAt(Instant.now().plus(inviteTokenExpiryHours, ChronoUnit.HOURS))
          .used(false)
          .build();
    inviteTokenRepository.save(inviteToken);

    emailService.sendInviteEmail(employee.getEmail(), employee.getFirstName(), token);

    return employeeUtils.toEmployeeResponse(employee);
  }

  // --- Set password via invite link ---

  @Transactional
  public void setPassword(SetPasswordRequest request) {
    InviteToken inviteToken = inviteTokenRepository.findByToken(request.token())
          .orElseThrow(() -> new RuntimeException("Invalid or expired invite link"));

    if (inviteToken.isUsed()) throw new RuntimeException("This invite link has already been used");
    if (inviteToken.getExpiresAt().isBefore(Instant.now())) throw new RuntimeException("This invite link has expired");

    Employee employee = inviteToken.getEmployee();
    employee.setPassword(passwordEncoder.encode(request.password()));
    employee.setStatus(EmployeeStatus.ACTIVE);
    employeeRepository.save(employee);

    inviteToken.setUsed(true);
    inviteTokenRepository.save(inviteToken);
  }

  // --- Forgot password ---

  @Transactional
  public void requestPasswordReset(ForgotPasswordRequest request) {
    Employee employee = employeeRepository.findByEmail(request.email())
          .orElseThrow(() -> new UsernameNotFoundException("No account found with that email"));

    // Reuse invite token table for reset tokens (same mechanism, different email)
    inviteTokenRepository.deleteByEmployeeId(employee.getId());

    String token = UUID.randomUUID().toString();
    InviteToken resetToken = InviteToken.builder()
          .token(token)
          .employee(employee)
          .expiresAt(Instant.now().plus(resetTokenExpiryHours, ChronoUnit.HOURS))
          .used(false)
          .build();
    inviteTokenRepository.save(resetToken);

    emailService.sendPasswordResetEmail(employee.getEmail(), employee.getFirstName(), token);
  }

  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    InviteToken resetToken = inviteTokenRepository.findByToken(request.token())
          .orElseThrow(() -> new RuntimeException("Invalid or expired reset link"));

    if (resetToken.isUsed()) throw new RuntimeException("This reset link has already been used");
    if (resetToken.getExpiresAt().isBefore(Instant.now())) throw new RuntimeException("This reset link has expired");

    Employee employee = resetToken.getEmployee();
    employee.setPassword(passwordEncoder.encode(request.password()));
    employeeRepository.save(employee);

    resetToken.setUsed(true);
    inviteTokenRepository.save(resetToken);
  }


  @Override
  public EmployeeResponse getEmployeeById(UUID id) {
    Employee employee = employeeRepository.findById(id)
          .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

    return new EmployeeResponse(
          employee.getId(),
          safe(employee.getFirstName()),
          safe(employee.getLastName()),
          employee.getEmail(),
          employee.getPhoneNumber(),
          employee.getRole(),
          employee.getGender()
    );
  }

  @Override
  public List<EmployeeResponse> getEmployeesByIds(List<UUID> ids, EmployeeRequest employeeRequest) {
    List<Employee> employees;

    if (ids == null || ids.isEmpty()) {
      int page = employeeRequest.getPage() != null ? employeeRequest.getPage() : 0;
      int size = employeeRequest.getSize() != null ? employeeRequest.getSize() : 10;

      Pageable pageable = PageRequest.of(page, size);
      employees = employeeRepository.findAll(pageable).getContent();
    } else {
      employees = employeeRepository.findAllById(ids);
    }

    String filter = employeeRequest.getFilter();

    List<Employee> filterEmployee = employees.stream()
          .filter(employee -> filter == null || filter.isBlank()
                || safe(employee.getFirstName()).toLowerCase().contains(filter.toLowerCase()))
          .collect(Collectors.toList());

    return filterEmployee.stream()
          .map(employee -> new EmployeeResponse(
                employee.getId(),
                safe(employee.getFirstName()),
                safe(employee.getLastName()),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getRole(),
                employee.getGender()))
          .collect(Collectors.toList());
  }


  @Override
  public EmployeeResponse updateProfile(UUID id, UpdateEmployee updateEmployee) {
    Employee employee = employeeRepository.findById(id)
          .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

    if (updateEmployee.firstName() != null && !updateEmployee.firstName().isBlank()) {
      employee.setFirstName(updateEmployee.firstName());
    }
    if (updateEmployee.lastName() != null && !updateEmployee.lastName().isBlank()) {
      employee.setLastName(updateEmployee.lastName());
    }
    if (updateEmployee.email() != null && !updateEmployee.email().isBlank()) {
      employee.setEmail(updateEmployee.email());
    }
    if (updateEmployee.phoneNumber() != null && !updateEmployee.phoneNumber().isBlank()) {
      employee.setPhoneNumber(updateEmployee.phoneNumber());
    }
    if (updateEmployee.role() != null) {
      employee.setRole(updateEmployee.role());
    }
    if (updateEmployee.gender() != null) {
      employee.setGender(updateEmployee.gender());
    }
    Employee savedEmployee = employeeRepository.save(employee);

    return employeeUtils.toEmployeeResponse(savedEmployee);
  }

  @Override
  public void deleteEmployee(UUID id) {
    Employee employee = employeeRepository.findById(id)
          .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

    employeeRepository.delete(employee);
  }

  @Override
  public EmployeeResponse updateOwnProfile(String email, UpdateEmployee updateEmployee) {
    Employee employee = employeeRepository.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return updateProfile(employee.getId(), updateEmployee);
  }

  @Override
  public EmployeeResponse getEmployeeByEmail(String email) {
    Employee employee = employeeRepository.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return authMapper.toEmployeeResponse(employee);
  }

}