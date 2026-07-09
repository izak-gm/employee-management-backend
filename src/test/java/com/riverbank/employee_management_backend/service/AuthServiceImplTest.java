package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.AuthResponse;
import com.riverbank.employee_management_backend.dto.RegisterLoginRequest;
import com.riverbank.employee_management_backend.dto.UpdateEmployee;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.enums.Role;
import com.riverbank.employee_management_backend.exception.EmployeeNotFoundException;
import com.riverbank.employee_management_backend.mapper.AuthMapper;
import com.riverbank.employee_management_backend.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl covering:
 * - register()
 * - login()
 * - updateProfile()
 * - deleteEmployee()
 * <p>
 * Repository, mapper, JWT service, authentication manager, and password
 * encoder are all mocked so these tests exercise ONLY the service logic,
 * not real DB/security infrastructure.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl unit tests")
class AuthServiceImplTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private AuthMapper authMapper;

  @Mock
  private JwtService jwtService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthServiceImpl authService;

  private Employee employee;
  private UUID employeeId;

  @BeforeEach
  void setUp() {
    employeeId = UUID.randomUUID();
    employee = Employee.builder()
          .id(employeeId)
          .firstName("Isaac")
          .lastName("Maingi")
          .email("isaac@example.com")
          .phoneNumber("+254712345678")
          .password("encoded-password")
          .role(Role.EMPLOYEE)
          .build();
  }

  // ------------------------------------------------------------------
  // register()
  // ------------------------------------------------------------------
  @Nested
  @DisplayName("register()")
  class Register {

    @Test
    @DisplayName("returns a token when registration succeeds")
    void register_ShouldReturnAuthResponse_WhenValid() {
      RegisterLoginRequest request = new RegisterLoginRequest("isaac@example.com", "password123");

      when(authMapper.register(request)).thenReturn(employee);
      when(employeeRepository.save(employee)).thenReturn(employee);
      when(jwtService.generateToken(employee)).thenReturn("mocked-jwt-token");

      AuthResponse response = authService.register(request);

      assertThat(response).isNotNull();
      assertEquals("mocked-jwt-token", response.getToken());
      verify(employeeRepository, times(1)).save(employee);
      verify(jwtService, times(1)).generateToken(employee);
    }

    @Test
    @DisplayName("propagates repository failure without generating a token")
    void register_ShouldNotGenerateToken_WhenSaveFails() {
      RegisterLoginRequest request = new RegisterLoginRequest("isaac@example.com", "password123");

      when(authMapper.register(request)).thenReturn(employee);
      when(employeeRepository.save(employee)).thenThrow(new RuntimeException("DB error"));

      assertThrows(RuntimeException.class, () -> authService.register(request));

      verify(jwtService, never()).generateToken(any());
    }
  }

  // ------------------------------------------------------------------
  // login()
  // ------------------------------------------------------------------
  @Nested
  @DisplayName("login()")
  class Login {

    @Test
    @DisplayName("returns a token when credentials are valid")
    void login_ShouldReturnAuthResponse_WhenCredentialsValid() {
      RegisterLoginRequest request = new RegisterLoginRequest("isaac@example.com", "password123");

      when(employeeRepository.findByEmail("isaac@example.com"))
            .thenReturn(Optional.of(employee));
      when(jwtService.generateToken(employee)).thenReturn("mocked-jwt-token");

      AuthResponse response = authService.login(request);

      assertThat(response).isNotNull();
      assertEquals("mocked-jwt-token", response.getToken());

      ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
            ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
      verify(authenticationManager, times(1)).authenticate(captor.capture());
      assertEquals("isaac@example.com", captor.getValue().getPrincipal());
    }

    @Test
    @DisplayName("throws when the authenticated email has no matching employee")
    void login_ShouldThrowException_WhenUserNotFound() {
      RegisterLoginRequest request = new RegisterLoginRequest("ghost@example.com", "password123");

      when(employeeRepository.findByEmail("ghost@example.com"))
            .thenReturn(Optional.empty());

      assertThrows(UsernameNotFoundException.class, () -> authService.login(request));

      verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("propagates bad-credentials failure from the authentication manager")
    void login_ShouldPropagateAuthenticationFailure() {
      RegisterLoginRequest request = new RegisterLoginRequest("isaac@example.com", "wrong-password");

      doThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"))
            .when(authenticationManager).authenticate(any());

      assertThrows(
            org.springframework.security.authentication.BadCredentialsException.class,
            () -> authService.login(request)
      );

      verify(employeeRepository, never()).findByEmail(anyString());
    }
  }

  // ------------------------------------------------------------------
  // updateProfile()
  // ------------------------------------------------------------------
  @Nested
  @DisplayName("updateProfile()")
  class UpdateProfile {

    @Test
    @DisplayName("updates only the fields provided (partial update)")
    void updateProfile_ShouldUpdateOnlyProvidedFields() {
      UpdateEmployee update = new UpdateEmployee("Isaac Updated", null, null, null, null);

      when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
      when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

      Employee result = authService.updateProfile(employeeId, update);

      assertEquals("Isaac Updated", result.getFirstName());
      assertEquals("Maingi", result.getLastName()); // unchanged
      assertEquals("isaac@example.com", result.getEmail()); // unchanged
      verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("encodes and updates the password when provided")
    void updateProfile_ShouldEncodePassword_WhenPasswordProvided() {
      UpdateEmployee update = new UpdateEmployee(null, null, null, null, "newPlainPassword");

      when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
      when(passwordEncoder.encode("newPlainPassword")).thenReturn("newEncodedPassword");
      when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

      Employee result = authService.updateProfile(employeeId, update);

      assertEquals("newEncodedPassword", result.getPassword());
      verify(passwordEncoder, times(1)).encode("newPlainPassword");
    }

    @Test
    @DisplayName("leaves password untouched when not provided")
    void updateProfile_ShouldSkipPassword_WhenNotProvided() {
      UpdateEmployee update = new UpdateEmployee("Isaac Updated", null, null, null, null);

      when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
      when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

      Employee result = authService.updateProfile(employeeId, update);

      assertEquals("encoded-password", result.getPassword()); // unchanged
      verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("throws EmployeeNotFoundException when the employee does not exist")
    void updateProfile_ShouldThrowException_WhenEmployeeNotFound() {
      UpdateEmployee update = new UpdateEmployee("Isaac Updated", null, null, null, null);
      UUID missingId = UUID.randomUUID();

      when(employeeRepository.findById(missingId)).thenReturn(Optional.empty());

      assertThrows(EmployeeNotFoundException.class,
            () -> authService.updateProfile(missingId, update));

      verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("ignores blank string fields instead of overwriting with empty values")
    void updateProfile_ShouldIgnoreBlankFields() {
      UpdateEmployee update = new UpdateEmployee("", "   ", null, null, null);

      when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
      when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

      Employee result = authService.updateProfile(employeeId, update);

      assertEquals("Isaac", result.getFirstName()); // unchanged, blank ignored
      assertEquals("Maingi", result.getLastName());  // unchanged, blank ignored
    }
  }

  // ------------------------------------------------------------------
  // deleteEmployee()
  // ------------------------------------------------------------------
  @Nested
  @DisplayName("deleteEmployee()")
  class DeleteEmployee {

    @Test
    @DisplayName("deletes the employee when found")
    void deleteEmployee_ShouldDeleteEmployee_WhenExists() {
      when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

      authService.deleteEmployee(employeeId);

      verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    @DisplayName("throws EmployeeNotFoundException and never deletes when not found")
    void deleteEmployee_ShouldThrowException_WhenNotFound() {
      UUID missingId = UUID.randomUUID();
      when(employeeRepository.findById(missingId)).thenReturn(Optional.empty());

      assertThrows(EmployeeNotFoundException.class,
            () -> authService.deleteEmployee(missingId));

      verify(employeeRepository, never()).delete(any());
    }
  }
}