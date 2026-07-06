package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.AdminRegisterRequest;
import com.riverbank.employee_management_backend.dto.AuthResponse;
import com.riverbank.employee_management_backend.dto.RegisterRequest;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.enums.Auth;
import com.riverbank.employee_management_backend.mapper.AuthMapper;
import com.riverbank.employee_management_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private static final Map<Auth, Set<Auth>> Auth_HIERARCHY = Map.of(
        Auth.SUPERADMIN, Set.of(Auth.ADMIN, Auth.SUPERADMIN, Auth.DEVELOPER),
        Auth.ADMIN, Set.of(Auth.DEVELOPER)
  );

  private final EmployeeRepository repository;
  private final AuthMapper authMapper;
  private final JwtService jwtService;

  @Override
  public AuthResponse register(RegisterRequest registerRequest) {
    Employee employee = repository.save(authMapper.register(registerRequest));
    var jwtToken = jwtService.generateToken(employee);
    return AuthResponse.builder()
          .token(jwtToken)
          .build();
  }

  @Override
  public AuthResponse registerAdmin(AdminRegisterRequest request, UserDetails currentUser) {
    validateRoleAssignment(currentUser, request.auth());
    Employee employee = repository.save(authMapper.registerAdmin(request));
    var jwtToken = jwtService.generateToken(employee);
    return AuthResponse.builder().token(jwtToken).build();
  }

  @Override
  public void validateRoleAssignment(UserDetails currentUser, Auth requestedAuth) {
    Auth callerAuth = extractAuth(currentUser); // pull from authorities
    Set<Auth> allowed = Auth_HIERARCHY.getOrDefault(callerAuth, Set.of());

    if (!allowed.contains(requestedAuth)) {
      throw new AccessDeniedException("Cannot assign Auth: " + requestedAuth);
    }
  }

  private Auth extractAuth(UserDetails currentUser) {
    return currentUser.getAuthorities().stream()
          .findFirst()
          .map(a -> Auth.valueOf(a.getAuthority().replace("ROLE_", "")))
          .orElseThrow(() -> new AccessDeniedException("No role found"));
  }

}
