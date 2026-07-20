package com.riverbank.employee_management_backend.controller;

import com.riverbank.employee_management_backend.dto.payroll.PayrollProfileRequest;
import com.riverbank.employee_management_backend.dto.payroll.PayrollProfileResponse;
import com.riverbank.employee_management_backend.service.payroll.PayrollProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payroll/profiles")
@RequiredArgsConstructor
@Tag(name = "Employee Payment Profile")
public class PayrollProfileController {

  private final PayrollProfileService payrollProfileService;

  // ── Create ─────────────────────────────────────────────────────────────────
  // POST /api/v1/payroll/profiles

  @PostMapping
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN', 'PAYROLL_MANAGER')")
  public ResponseEntity<PayrollProfileResponse> create(
        @Valid @RequestBody PayrollProfileRequest request
  ) {
    return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(payrollProfileService.createProfile(request));
  }

  // ── Update ─────────────────────────────────────────────────────────────────
  // PUT /api/v1/payroll/profiles/{profileId}

  @PutMapping("/{profileId}")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN', 'PAYROLL_MANAGER')")
  public ResponseEntity<PayrollProfileResponse> update(
        @PathVariable UUID profileId,
        @Valid @RequestBody PayrollProfileRequest request
  ) {
    return ResponseEntity.ok(payrollProfileService.updateProfile(profileId, request));
  }

  // ── Get by employee ────────────────────────────────────────────────────────
  // GET /api/v1/payroll/profiles/employee/{employeeId}

  @GetMapping("/employee/{employeeId}")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN', 'PAYROLL_MANAGER', 'FINANCE_MANAGER')")
  public ResponseEntity<PayrollProfileResponse> getByEmployee(
        @PathVariable UUID employeeId
  ) {
    return ResponseEntity.ok(payrollProfileService.getProfileByEmployeeId(employeeId));
  }

  // ── Deactivate ─────────────────────────────────────────────────────────────
  // DELETE /api/v1/payroll/profiles/{profileId}

  @DeleteMapping("/{profileId}")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN')")
  public ResponseEntity<Void> deactivate(@PathVariable UUID profileId) {
    payrollProfileService.deactivateProfile(profileId);
    return ResponseEntity.noContent().build();
  }
}
