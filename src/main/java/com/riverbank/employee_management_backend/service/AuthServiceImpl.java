package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.*;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.enums.Role;
import com.riverbank.employee_management_backend.mapper.AuthMapper;
import com.riverbank.employee_management_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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


  @Override
  public AuthResponse register(RegisterLoginRequest registerLoginRequest) {
    Employee employee = employeeRepository.save(authMapper.register(registerLoginRequest));
    var jwtToken = jwtService.generateToken(employee);
    return AuthResponse.builder()
          .token(jwtToken)
          .build();
  }

  @Override
  public AuthResponse registerAdmin(AdminRegisterRequest request, UserDetails currentUser) {
    validateRoleAssignment(currentUser, request.role());
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

  @Override
  public List<EmployeeResponse> getEmployeesByIds(List<UUID> ids, EmployeeRequest employeeRequest) {
    List<Employee> employees;

    if (ids == null || ids.isEmpty()) {
      employees = employeeRepository.findAll();
    } else {
      employees = employeeRepository.findAllById(ids);
    }

    List<Employee> filterEmployee = employees.stream()
          .filter(employee -> employeeRequest.getFilter() == null
                || employee.getFirstName().toLowerCase().contains(employeeRequest.getFilter().toLowerCase()))
          .collect(Collectors.toList());
    return filterEmployee.stream()
          .map(employee -> new EmployeeResponse(
                employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getEmail(), employee.getPhoneNumber(),
                employee.getRole()
          ))
          .collect(Collectors.toList());
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

  private Role extractAuth(UserDetails currentUser) {
    return currentUser.getAuthorities().stream()
          .findFirst()
          .map(a -> Role.valueOf(a.getAuthority().replace("ROLE_", "")))
          .orElseThrow(() -> new AccessDeniedException("No role found"));
  }

}
