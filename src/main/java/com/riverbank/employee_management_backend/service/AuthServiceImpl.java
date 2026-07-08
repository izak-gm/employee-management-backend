package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.*;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.enums.Role;
import com.riverbank.employee_management_backend.exception.EmployeeNotFoundException;
import com.riverbank.employee_management_backend.exception.UserAlreadyExistsException;
import com.riverbank.employee_management_backend.mapper.AuthMapper;
import com.riverbank.employee_management_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.riverbank.employee_management_backend.util.StringUtils.safe;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private static final Map<Role, Set<Role>> Auth_HIERARCHY = Map.of(
        Role.SUPERADMIN, Set.of(Role.ADMIN, Role.SUPERADMIN, Role.DEVELOPER),
        Role.ADMIN, Set.of(Role.DEVELOPER)
  );

  private final EmployeeRepository employeeRepository;
  private final AuthMapper authMapper;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder; // inject this

  @Override
  public AuthResponse register(RegisterLoginRequest registerLoginRequest) {
    if (employeeRepository.existsByEmail(registerLoginRequest.email())) {
      throw new UserAlreadyExistsException("User already exists");
    }
    Employee employee = employeeRepository.save(authMapper.register(registerLoginRequest));
    var jwtToken = jwtService.generateToken(employee);
    return AuthResponse.builder()
          .token(jwtToken)
          .build();
  }

  @Override
  public AuthResponse registerAdmin(AdminRegisterRequest request, UserDetails currentUser) {
    validateRoleAssignment(currentUser, request.role());
    if (employeeRepository.existsByEmail(request.email())) {
      throw new UserAlreadyExistsException("User already exists");
    }
    Employee employee = employeeRepository.save(authMapper.registerAdmin(request));
    var jwtToken = jwtService.generateToken(employee);
    return AuthResponse.builder().token(jwtToken).build();
  }

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
  public AuthResponse login(RegisterLoginRequest registerLoginRequest) {
    authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                registerLoginRequest.email(),
                registerLoginRequest.password()
          )
    );
    var employee = employeeRepository.findByEmail(registerLoginRequest.email())
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    var jwtToken = jwtService.generateToken(employee);
    return AuthResponse.builder()
          .token(jwtToken)
          .build();
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
          employee.getRole()
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
                employee.getRole()
          ))
          .collect(Collectors.toList());
  }


  @Override
  public Employee updateProfile(UUID id, UpdateEmployee updateEmployee) {
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
    if (updateEmployee.password() != null && !updateEmployee.password().isBlank()) {
      employee.setPassword(passwordEncoder.encode(updateEmployee.password()));
    }

    return employeeRepository.save(employee);
  }
  
  @Override
  public void deleteEmployee(UUID id) {
    Employee employee = employeeRepository.findById(id)
          .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

    employeeRepository.delete(employee);
  }
}