package com.riverbank.employee_management_backend.controller;

import com.riverbank.employee_management_backend.dto.payroll.*;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.service.payroll.PayrollService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
@Tag(name = "Payrolls")
public class PayrollController {

  private final PayrollService payrollService;

  // ── Admin: Generate payroll ────────────────────────────────────────────────
  // POST /api/v1/payroll/generate
  // Body: { month, year, employeeIds? }
  // Role: PAYROLL_MANAGER, SUPERADMIN, HR_ADMIN

  @PostMapping("/generate")
  @PreAuthorize("hasAnyRole('SUPERADMIN','HR_ADMIN','PAYROLL_MANAGER')")
  public ResponseEntity<List<PayrollSummaryResponse>> generate(
        @Valid @RequestBody GeneratePayrollRequest request,
        @AuthenticationPrincipal Employee currentUser
  ) {
    return ResponseEntity.ok(payrollService.generatePayroll(request, currentUser));
  }

  // ── Admin: Approve ────────────────────────────────────────────────────────
  // PUT /api/v1/payroll/{payrollId}/approve

  @PutMapping("/{payrollId}/approve")
  @PreAuthorize("hasAnyRole('SUPERADMIN','HR_ADMIN','PAYROLL_MANAGER')")
  public ResponseEntity<PayrollResponse> approve(
        @PathVariable UUID payrollId,
        @AuthenticationPrincipal Employee currentUser
  ) {
    return ResponseEntity.ok(payrollService.approvePayroll(payrollId, currentUser));
  }

  // ── Finance: Mark as paid ─────────────────────────────────────────────────
  // PUT /api/v1/payroll/{payrollId}/mark-paid

  @PutMapping("/{payrollId}/mark-paid")
  @PreAuthorize("hasAnyRole('SUPERADMIN','FINANCE_MANAGER')")
  public ResponseEntity<PayrollResponse> markAsPaid(
        @PathVariable UUID payrollId,
        @Valid @RequestBody MarkAsPaidRequest request,
        @AuthenticationPrincipal Employee currentUser
  ) {
    return ResponseEntity.ok(payrollService.markAsPaid(payrollId, request, currentUser));
  }

  // ── Admin: Reverse ────────────────────────────────────────────────────────
  // PUT /api/v1/payroll/{payrollId}/reverse

  @PutMapping("/{payrollId}/reverse")
  @PreAuthorize("hasAnyRole('SUPERADMIN','PAYROLL_MANAGER')")
  public ResponseEntity<PayrollResponse> reverse(
        @PathVariable UUID payrollId,
        @Valid @RequestBody ReversePayrollRequest request,
        @AuthenticationPrincipal Employee currentUser
  ) {
    return ResponseEntity.ok(payrollService.reversePayroll(payrollId, request, currentUser));
  }

  // ── Admin: Get all for a month/year ───────────────────────────────────────
  // GET /api/v1/payroll?month=3&year=2025

  @GetMapping
  @PreAuthorize("hasAnyRole('SUPERADMIN','HR_ADMIN','PAYROLL_MANAGER','FINANCE_MANAGER')")
  public ResponseEntity<List<PayrollSummaryResponse>> getByMonthAndYear(
        @RequestParam int month,
        @RequestParam int year
  ) {
    return ResponseEntity.ok(payrollService.getPayrollsByMonthAndYear(month, year));
  }

  // ── Admin: Get single payroll ─────────────────────────────────────────────
  // GET /api/v1/payroll/{payrollId}

  @GetMapping("/{payrollId}")
  @PreAuthorize("hasAnyRole('SUPERADMIN','HR_ADMIN','PAYROLL_MANAGER','FINANCE_MANAGER')")
  public ResponseEntity<PayrollResponse> getById(@PathVariable UUID payrollId) {
    return ResponseEntity.ok(payrollService.getPayrollById(payrollId));
  }

  // ── Admin: Re-send payslip email ──────────────────────────────────────────
  // POST /api/v1/payroll/{payrollId}/resend-payslip

  @PostMapping("/{payrollId}/resend-payslip")
  @PreAuthorize("hasAnyRole('SUPERADMIN','HR_ADMIN','PAYROLL_MANAGER')")
  public ResponseEntity<Void> resendPayslip(@PathVariable UUID payrollId) {
    payrollService.resendPayslip(payrollId);
    return ResponseEntity.ok().build();
  }

  // ── Admin: Download payslip PDF ───────────────────────────────────────────
  // GET /api/v1/payroll/{payrollId}/payslip

  @GetMapping("/{payrollId}/payslip")
  @PreAuthorize("hasAnyRole('SUPERADMIN','HR_ADMIN','PAYROLL_MANAGER','FINANCE_MANAGER')")
  public ResponseEntity<byte[]> downloadPayslip(@PathVariable UUID payrollId) {
    byte[] pdf = payrollService.downloadPayslip(payrollId);
    return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=payslip-" + payrollId + ".pdf")
          .contentType(MediaType.APPLICATION_PDF)
          .body(pdf);
  }

  // ── Employee: My payrolls ─────────────────────────────────────────────────
  // GET /api/v1/payroll/me

  @GetMapping("/me")
  public ResponseEntity<List<PayrollSummaryResponse>> getMyPayrolls(
        @AuthenticationPrincipal Employee currentUser
  ) {
    return ResponseEntity.ok(payrollService.getMyPayrolls(currentUser.getId()));
  }

  // ── Employee: My payroll for a specific period ────────────────────────────
  // GET /api/v1/payroll/me/{month}/{year}

  @GetMapping("/me/{month}/{year}")
  public ResponseEntity<PayrollResponse> getMyPayrollForPeriod(
        @PathVariable int month,
        @PathVariable int year,
        @AuthenticationPrincipal Employee currentUser
  ) {
    return ResponseEntity.ok(
          payrollService.getMyPayrollForPeriod(currentUser.getId(), month, year)
    );
  }

  // ── Employee: Download own payslip ────────────────────────────────────────
  // GET /api/v1/payroll/me/{month}/{year}/payslip

  @GetMapping("/me/{month}/{year}/payslip")
  public ResponseEntity<byte[]> downloadMyPayslip(
        @PathVariable int month,
        @PathVariable int year,
        @AuthenticationPrincipal Employee currentUser
  ) {
    PayrollResponse payroll = payrollService.getMyPayrollForPeriod(
          currentUser.getId(), month, year
    );
    byte[] pdf = payrollService.downloadPayslip(payroll.id());
    return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=payslip-%d-%02d.pdf".formatted(year, month))
          .contentType(MediaType.APPLICATION_PDF)
          .body(pdf);
  }
}