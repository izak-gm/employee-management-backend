package com.riverbank.employee_management_backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

  // ── Public — no token required ─────────────────────────────────────────────
  private static final String[] PUBLIC_URLS = {
        "/api/v1/auth/login",
        "/api/v1/auth/forgot-password",
        "/api/v1/auth/reset-password",
        "/api/v1/auth/setup-password",
        "/swagger-ui/**",
        "/v3/api-docs/**",
  };

  // ── Admin only — SUPERADMIN, HR_ADMIN ─────────────────────────────────────
  private static final String[] ADMIN_URLS = {
        "/api/v1/auth/admin/**",
  };

  // ── HR / Department management ────────────────────────────────────────────
  // Fine-grained control is handled by @PreAuthorize on each controller method
  private static final String[] HR_MANAGEMENT_URLS = {
        "/api/v1/employees/**",
        "/api/v1/departments/**",
        "/api/v1/positions/**",
  };

  // ── Payroll — split by role ────────────────────────────────────────────────
  // Employee self-service (any authenticated user)
  private static final String[] PAYROLL_EMPLOYEE_URLS = {
        "/api/v1/payroll/me",
        "/api/v1/payroll/me/**",
  };

  // Admin payroll management (role checked via @PreAuthorize in controller)
  private static final String[] PAYROLL_ADMIN_URLS = {
        "/api/v1/payroll/**",
  };

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CorsConfigurationSource corsConfigurationSource;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
          .cors(cors -> cors.configurationSource(corsConfigurationSource))
          .csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(auth -> auth

                // 1. Public — no auth needed
                .requestMatchers(PUBLIC_URLS).permitAll()

                // 2. Pure admin routes
                .requestMatchers(ADMIN_URLS)
                .hasAnyRole("SUPERADMIN", "HR_ADMIN")

                // 3. Employee self-service payroll — any logged-in user
                .requestMatchers(PAYROLL_EMPLOYEE_URLS)
                .authenticated()

                // 4. Payroll admin routes — SUPERADMIN, HR_ADMIN, PAYROLL_MANAGER, FINANCE_MANAGER
                //    Fine-grained control (e.g. only FINANCE_MANAGER can mark-paid)
                //    is handled by @PreAuthorize on each controller method
                .requestMatchers(PAYROLL_ADMIN_URLS)
                .hasAnyRole("SUPERADMIN", "HR_ADMIN", "PAYROLL_MANAGER", "FINANCE_MANAGER")

                // 5. HR management — any authenticated user (controller refines further)
                .requestMatchers(HR_MANAGEMENT_URLS)
                .authenticated()

                // 6. Everything else — must be authenticated
                .anyRequest().authenticated()
          )
          .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
          .build();
  }
}