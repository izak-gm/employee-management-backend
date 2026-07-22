package com.riverbank.employee_management_backend.service.payroll;

import com.riverbank.employee_management_backend.dto.payroll.PayrollProfileRequest;
import com.riverbank.employee_management_backend.dto.payroll.PayrollProfileResponse;

import java.util.List;
import java.util.UUID;

public interface PayrollProfileService {

  /**
   * Create a new payroll profile for an employee
   */
  PayrollProfileResponse createProfile(PayrollProfileRequest request);

  /**
   * Update an existing payroll profile
   */
  PayrollProfileResponse updateProfile(UUID profileId, PayrollProfileRequest request);

  /**
   * Get the active profile for an employee
   */
  PayrollProfileResponse getProfileByEmployeeId(UUID employeeId);

  /**
   * Deactivate a profile (e.g. when employee exits)
   */
  void deactivateProfile(UUID profileId);

  PayrollProfileResponse getPayrollProfileById(UUID profileId);

  List<PayrollProfileResponse> getAllPayrollProfiles();
}
