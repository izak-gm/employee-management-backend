package com.riverbank.employee_management_backend.controller;

import com.riverbank.employee_management_backend.dto.payroll.DeductionTypeRequest;
import com.riverbank.employee_management_backend.dto.payroll.DeductionTypeResponse;
import com.riverbank.employee_management_backend.dto.payroll.EarningTypeRequest;
import com.riverbank.employee_management_backend.dto.payroll.EarningTypeResponse;
import com.riverbank.employee_management_backend.service.EarningDeductionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Earnings and Deduction")
public class EarningDeductionController {

  private final EarningDeductionService earningDeductionService;

  // ══════════════════════════════════════════════════════════════════════════
  // EARNING TYPES  →  /api/v1/payroll/earning-types
  // ══════════════════════════════════════════════════════════════════════════

  @PostMapping("/api/v1/payroll/earning-types")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN', 'PAYROLL_MANAGER')")
  public ResponseEntity<EarningTypeResponse> createEarningType(
        @Valid @RequestBody EarningTypeRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
          .body(earningDeductionService.createEarningType(request));
  }

  @PutMapping("/api/v1/payroll/earning-types/{id}")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN', 'PAYROLL_MANAGER')")
  public ResponseEntity<EarningTypeResponse> updateEarningType(
        @PathVariable UUID id,
        @Valid @RequestBody EarningTypeRequest request
  ) {
    return ResponseEntity.ok(earningDeductionService.updateEarningType(id, request));
  }

  @GetMapping("/api/v1/payroll/earning-types/{id}")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN', 'PAYROLL_MANAGER', 'FINANCE_MANAGER')")
  public ResponseEntity<EarningTypeResponse> getEarningTypeById(@PathVariable UUID id) {
    return ResponseEntity.ok(earningDeductionService.getEarningTypeById(id));
  }

  // All types including inactive — admin view
  @GetMapping("/api/v1/payroll/earning-types")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN', 'PAYROLL_MANAGER', 'FINANCE_MANAGER')")
  public ResponseEntity<List<EarningTypeResponse>> getAllEarningTypes() {
    return ResponseEntity.ok(earningDeductionService.getAllEarningTypes());
  }

  // Active types only — for dropdowns / payroll generation
  @GetMapping("/api/v1/payroll/earning-types/active")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<EarningTypeResponse>> getActiveEarningTypes() {
    return ResponseEntity.ok(earningDeductionService.getActiveEarningTypes());
  }

  @PatchMapping("/api/v1/payroll/earning-types/{id}/deactivate")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN')")
  public ResponseEntity<Void> deactivateEarningType(@PathVariable UUID id) {
    earningDeductionService.deactivateEarningType(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/api/v1/payroll/earning-types/{id}/activate")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN')")
  public ResponseEntity<Void> activateEarningType(@PathVariable UUID id) {
    earningDeductionService.activateEarningType(id);
    return ResponseEntity.noContent().build();
  }

  // ══════════════════════════════════════════════════════════════════════════
  // DEDUCTION TYPES  →  /api/v1/payroll/deduction-types
  // ══════════════════════════════════════════════════════════════════════════

  @PostMapping("/api/v1/payroll/deduction-types")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN', 'PAYROLL_MANAGER')")
  public ResponseEntity<DeductionTypeResponse> createDeductionType(
        @Valid @RequestBody DeductionTypeRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
          .body(earningDeductionService.createDeductionType(request));
  }

  @PutMapping("/api/v1/payroll/deduction-types/{id}")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN', 'PAYROLL_MANAGER')")
  public ResponseEntity<DeductionTypeResponse> updateDeductionType(
        @PathVariable UUID id,
        @Valid @RequestBody DeductionTypeRequest request
  ) {
    return ResponseEntity.ok(earningDeductionService.updateDeductionType(id, request));
  }

  @GetMapping("/api/v1/payroll/deduction-types/{id}")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN', 'PAYROLL_MANAGER', 'FINANCE_MANAGER')")
  public ResponseEntity<DeductionTypeResponse> getDeductionTypeById(@PathVariable UUID id) {
    return ResponseEntity.ok(earningDeductionService.getDeductionTypeById(id));
  }

  // All types including inactive — admin view
  @GetMapping("/api/v1/payroll/deduction-types")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN', 'PAYROLL_MANAGER', 'FINANCE_MANAGER')")
  public ResponseEntity<List<DeductionTypeResponse>> getAllDeductionTypes() {
    return ResponseEntity.ok(earningDeductionService.getAllDeductionTypes());
  }

  // Active types only — for dropdowns / payroll generation
  @GetMapping("/api/v1/payroll/deduction-types/active")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<DeductionTypeResponse>> getActiveDeductionTypes() {
    return ResponseEntity.ok(earningDeductionService.getActiveDeductionTypes());
  }

  @PatchMapping("/api/v1/payroll/deduction-types/{id}/deactivate")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN')")
  public ResponseEntity<Void> deactivateDeductionType(@PathVariable UUID id) {
    earningDeductionService.deactivateDeductionType(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/api/v1/payroll/deduction-types/{id}/activate")
  @PreAuthorize("hasAnyRole('SUPERADMIN', 'HR_ADMIN')")
  public ResponseEntity<Void> activateDeductionType(@PathVariable UUID id) {
    earningDeductionService.activateDeductionType(id);
    return ResponseEntity.noContent().build();
  }
}