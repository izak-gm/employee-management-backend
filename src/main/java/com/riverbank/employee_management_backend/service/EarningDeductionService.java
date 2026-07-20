package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.payroll.DeductionTypeRequest;
import com.riverbank.employee_management_backend.dto.payroll.DeductionTypeResponse;
import com.riverbank.employee_management_backend.dto.payroll.EarningTypeRequest;
import com.riverbank.employee_management_backend.dto.payroll.EarningTypeResponse;

import java.util.List;
import java.util.UUID;

public interface EarningDeductionService {

  // ── Earning Types ──────────────────────────────────────────────────────────

  EarningTypeResponse createEarningType(EarningTypeRequest request);

  EarningTypeResponse updateEarningType(UUID id, EarningTypeRequest request);

  EarningTypeResponse getEarningTypeById(UUID id);

  List<EarningTypeResponse> getAllEarningTypes();

  List<EarningTypeResponse> getActiveEarningTypes();

  void deactivateEarningType(UUID id);

  void activateEarningType(UUID id);

  // ── Deduction Types ────────────────────────────────────────────────────────

  DeductionTypeResponse createDeductionType(DeductionTypeRequest request);

  DeductionTypeResponse updateDeductionType(UUID id, DeductionTypeRequest request);

  DeductionTypeResponse getDeductionTypeById(UUID id);

  List<DeductionTypeResponse> getAllDeductionTypes();

  List<DeductionTypeResponse> getActiveDeductionTypes();

  void deactivateDeductionType(UUID id);

  void activateDeductionType(UUID id);
}