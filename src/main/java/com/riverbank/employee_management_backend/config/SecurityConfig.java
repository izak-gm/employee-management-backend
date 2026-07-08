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
@EnableMethodSecurity  // <-- required for @PreAuthorize to be evaluated
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
  // open routes
  private static final String[] PUBLIC_URL = {
        "/api/v1/auth/register",
        "/api/v1/auth/login",
        "/swagger-ui/**",
        "/v3/api-docs/**"
  };
  private static final String[] ADMIN_URL = {
        "/api/v1/auth/admin/**"
  };
  private static final String[] EMPLOYEES_URL = {
        "/api/v1/employees/**"
  };
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CorsConfigurationSource corsConfigurationSource;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
          .cors(cors -> cors.configurationSource(corsConfigurationSource))
          .csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URL).permitAll()
                .requestMatchers(ADMIN_URL).hasAnyRole("SUPERADMIN", "ADMIN")
                .requestMatchers(EMPLOYEES_URL).authenticated()
                .anyRequest().fullyAuthenticated()
          )
          .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
          .build();
  }
}
