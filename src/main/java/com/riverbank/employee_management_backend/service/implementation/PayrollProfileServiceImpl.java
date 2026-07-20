package com.riverbank.employee_management_backend.service.implementation;

import com.riverbank.employee_management_backend.dto.payroll.PayrollProfileRequest;
import com.riverbank.employee_management_backend.dto.payroll.PayrollProfileResponse;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.entity.payrolls.EmployeePayrollProfile;
import com.riverbank.employee_management_backend.mapper.PayrollProfileMapper;
import com.riverbank.employee_management_backend.repository.EmployeeRepository;
import com.riverbank.employee_management_backend.repository.payrolls.EmployeePayrollProfileRepository;
import com.riverbank.employee_management_backend.service.payroll.PayrollProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollProfileServiceImpl implements PayrollProfileService {

  private final EmployeePayrollProfileRepository profileRepo;
  private final EmployeeRepository employeeRepo;
  private final PayrollProfileMapper mapper;

  // ── Create ─────────────────────────────────────────────────────────────────

  @Override
  @Transactional
  public PayrollProfileResponse createProfile(PayrollProfileRequest request) {

    Employee employee = employeeRepo.findById(request.employeeId())
          .orElseThrow(() -> new RuntimeException("Employee not found: " + request.employeeId()));

    // One active profile per employee — deactivate any existing before creating
    profileRepo.findByEmployeeIdAndActiveTrue(request.employeeId())
          .ifPresent(existing -> {
            existing.setActive(false);
            profileRepo.save(existing);
            log.info("Deactivated existing payroll profile for employee {}",
                  employee.getEmployeeNumber());
          });

    // Validate unique constraints
    validateUniqueFields(request, null);

    EmployeePayrollProfile profile = EmployeePayrollProfile.builder()
          .employee(employee)
          .basicSalary(request.basicSalary())
          .houseAllowance(orZero(request.houseAllowance()))
          .transportAllowance(orZero(request.transportAllowance()))
          .medicalAllowance(orZero(request.medicalAllowance()))
          .otherAllowance(orZero(request.otherAllowance()))
          .pensionContribution(orZero(request.pensionContribution()))
          .bankName(request.bankName())
          .bankBranch(request.bankBranch())
          .accountNumber(request.accountNumber())
          .kraPin(request.kraPin())
          .shifNumber(request.shifNumber())
          .nssfNumber(request.nssfNumber())
          .effectiveFrom(request.effectiveFrom())
//                .effectiveTo(request.effectiveTo())
          .active(true)
          .build();

    log.info("Created payroll profile for employee {}", employee.getEmployeeNumber());
    return mapper.toResponse(profileRepo.save(profile));
  }

  // ── Update ─────────────────────────────────────────────────────────────────

  @Override
  @Transactional
  public PayrollProfileResponse updateProfile(UUID profileId, PayrollProfileRequest request) {

    EmployeePayrollProfile profile = profileRepo.findById(profileId)
          .orElseThrow(() -> new RuntimeException("Payroll profile not found: " + profileId));

    // Validate unique fields — exclude current profile from duplicate check
    validateUniqueFields(request, profileId);

    profile.setBasicSalary(request.basicSalary());
    profile.setHouseAllowance(orZero(request.houseAllowance()));
    profile.setTransportAllowance(orZero(request.transportAllowance()));
    profile.setMedicalAllowance(orZero(request.medicalAllowance()));
    profile.setOtherAllowance(orZero(request.otherAllowance()));
    profile.setPensionContribution(orZero(request.pensionContribution()));
    profile.setBankName(request.bankName());
    profile.setBankBranch(request.bankBranch());
    profile.setAccountNumber(request.accountNumber());
    profile.setKraPin(request.kraPin());
    profile.setShifNumber(request.shifNumber());
    profile.setNssfNumber(request.nssfNumber());
    profile.setEffectiveFrom(request.effectiveFrom());
//    profile.setEffectiveTo(request.effectiveTo());

    log.info("Updated payroll profile {} for employee {}",
          profileId, profile.getEmployee().getEmployeeNumber());

    return mapper.toResponse(profileRepo.save(profile));
  }

  // ── Query ──────────────────────────────────────────────────────────────────

  @Override
  @Transactional(readOnly = true)
  public PayrollProfileResponse getProfileByEmployeeId(UUID employeeId) {
    return profileRepo.findByEmployeeIdAndActiveTrue(employeeId)
          .map(mapper::toResponse)
          .orElseThrow(() -> new RuntimeException(
                "No active payroll profile found for employee: " + employeeId));
  }

  // ── Deactivate ─────────────────────────────────────────────────────────────

  @Override
  @Transactional
  public void deactivateProfile(UUID profileId) {
    EmployeePayrollProfile profile = profileRepo.findById(profileId)
          .orElseThrow(() -> new RuntimeException("Payroll profile not found: " + profileId));

    profile.setActive(false);
    profileRepo.save(profile);

    log.info("Deactivated payroll profile {} for employee {}",
          profileId, profile.getEmployee().getEmployeeNumber());
  }

  // ── Helpers ────────────────────────────────────────────────────────────────

  /**
   * Validates that KRA PIN, account number, SHIF, and NSSF numbers
   * are not already used by another profile.
   * Pass currentProfileId to exclude it from the check during updates.
   */
  private void validateUniqueFields(PayrollProfileRequest request, UUID currentProfileId) {

    profileRepo.findByKraPin(request.kraPin())
          .filter(p -> !p.getId().equals(currentProfileId))
          .ifPresent(p -> {
            throw new RuntimeException(
                  "KRA PIN " + request.kraPin() + " is already registered");
          });

    profileRepo.findByAccountNumber(request.accountNumber())
          .filter(p -> !p.getId().equals(currentProfileId))
          .ifPresent(p -> {
            throw new RuntimeException(
                  "Account number " + request.accountNumber() + " is already registered");
          });

    profileRepo.findByShifNumber(request.shifNumber())
          .filter(p -> !p.getId().equals(currentProfileId))
          .ifPresent(p -> {
            throw new RuntimeException(
                  "SHIF number " + request.shifNumber() + " is already registered");
          });

    profileRepo.findByNssfNumber(request.nssfNumber())
          .filter(p -> !p.getId().equals(currentProfileId))
          .ifPresent(p -> {
            throw new RuntimeException(
                  "NSSF number " + request.nssfNumber() + " is already registered");
          });
  }

  /**
   * Returns the value or BigDecimal.ZERO if null
   */
  private BigDecimal orZero(BigDecimal value) {
    return value != null ? value : BigDecimal.ZERO;
  }
}
