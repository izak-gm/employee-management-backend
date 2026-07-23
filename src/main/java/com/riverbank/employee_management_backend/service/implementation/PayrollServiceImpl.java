package com.riverbank.employee_management_backend.service.implementation;

import com.riverbank.employee_management_backend.dto.payroll.*;
import com.riverbank.employee_management_backend.dto.payroll.PAYE.TaxCalculation;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.entity.payrolls.*;
import com.riverbank.employee_management_backend.enums.EmployeeStatus;
import com.riverbank.employee_management_backend.enums.payrolls.PayrollStatus;
import com.riverbank.employee_management_backend.mapper.PayrollMapper;
import com.riverbank.employee_management_backend.repository.EmployeeRepository;
import com.riverbank.employee_management_backend.repository.payrolls.DeductionTypeRepository;
import com.riverbank.employee_management_backend.repository.payrolls.EarningTypeRepository;
import com.riverbank.employee_management_backend.repository.payrolls.EmployeePayrollProfileRepository;
import com.riverbank.employee_management_backend.repository.payrolls.PayrollRepository;
import com.riverbank.employee_management_backend.service.PayslipPdfService;
import com.riverbank.employee_management_backend.service.email.EmailService;
import com.riverbank.employee_management_backend.service.payroll.PayrollService;
import com.riverbank.employee_management_backend.util.PayrollCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

  private final PayrollRepository payrollRepo;
  private final EmployeePayrollProfileRepository profileRepo;
  private final EarningTypeRepository earningTypeRepo;
  private final DeductionTypeRepository deductionTypeRepo;
  private final EmployeeRepository employeeRepo;
  private final PayrollCalculator calculator;
  private final PayrollMapper mapper;
  private final EmailService emailService;
  private final PayslipPdfService payslipPdfService;

  // ── Generate ───────────────────────────────────────────────────────────────

  @Override
  @Transactional
  public List<PayrollSummaryResponse> generatePayroll(GeneratePayrollRequest request,
                                                      Employee generatedBy) {
    int month = request.month();
    int year = request.year();

    // Determine target employees
    List<Employee> employees = (request.employeeIds() != null && !request.employeeIds().isEmpty())
          ? employeeRepo.findAllById(request.employeeIds())
          : employeeRepo.findByStatus(EmployeeStatus.ACTIVE);

    List<PayrollSummaryResponse> results = new ArrayList<>();

    for (Employee employee : employees) {
      // Skip if already generated for this period
      if (payrollRepo.existsByEmployee_IdAndPayrollMonthAndPayrollYear(
            employee.getId(), month, year)) {
        log.warn("Payroll already exists for {} — {}/{}", employee.getEmployeeNumber(), month, year);
        continue;
      }

      // Fetch payroll profile
      Optional<EmployeePayrollProfile> profileOpt =
            profileRepo.findByEmployeeIdAndActiveTrue(employee.getId());

      if (profileOpt.isEmpty()) {
        log.warn("No active payroll profile for {} — skipping", employee.getEmployeeNumber());
        continue;
      }

      EmployeePayrollProfile profile = profileOpt.get();
      Payroll payroll = buildPayroll(employee, profile, month, year, generatedBy);
      payrollRepo.save(payroll);

      // Send payslip email (non-blocking — log failures, don't throw)
      sendPayslipSafely(payroll);

      results.add(mapper.toSummary(payroll));
    }

    return results;
  }

  // ── Approve ────────────────────────────────────────────────────────────────

  @Override
  @Transactional
  public PayrollResponse approvePayroll(UUID payrollId, Employee approver) {
    Payroll payroll = getByIdOrThrow(payrollId);
    assertStatus(payroll, PayrollStatus.GENERATED, "approve");

    payroll.setStatus(PayrollStatus.APPROVED);
    payroll.setApprovedBy(approver);
    payroll.setApprovedAt(LocalDateTime.now());

    return mapper.toResponse(payrollRepo.save(payroll));
  }

  // ── Mark as Paid ───────────────────────────────────────────────────────────

  @Override
  @Transactional
  public PayrollResponse markAsPaid(UUID payrollId, MarkAsPaidRequest request, Employee markedBy) {
    Payroll payroll = getByIdOrThrow(payrollId);
    assertStatus(payroll, PayrollStatus.APPROVED, "mark as paid");

    payroll.setStatus(PayrollStatus.PAID);
    payroll.setPaymentDate(LocalDate.now());
    payroll.setPaymentReference(request.paymentReference());

    return mapper.toResponse(payrollRepo.save(payroll));
  }

  // ── Reverse ────────────────────────────────────────────────────────────────

  @Override
  @Transactional
  public PayrollResponse reversePayroll(UUID payrollId, ReversePayrollRequest request,
                                        Employee reversedBy) {
    Payroll payroll = getByIdOrThrow(payrollId);
    assertStatus(payroll, PayrollStatus.APPROVED, "reverse");

    payroll.setStatus(PayrollStatus.REVERSED);
    payroll.setReversalReason(request.reason());
    payroll.setReversedBy(reversedBy);
    payroll.setReversedAt(LocalDateTime.now());

    return mapper.toResponse(payrollRepo.save(payroll));
  }

  // ── Queries ────────────────────────────────────────────────────────────────

  @Override
  @Transactional(readOnly = true)
  public PayrollResponse getPayrollById(UUID payrollId) {
    return mapper.toResponse(
          payrollRepo.findByIdWithDetails(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll not found: " + payrollId))
    );
  }

  @Override
  @Transactional(readOnly = true)
  public List<PayrollSummaryResponse> getPayrollsByMonthAndYear(int month, int year) {
    return payrollRepo.findByMonthAndYear(month, year)
          .stream().map(mapper::toSummary).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<PayrollSummaryResponse> getMyPayrolls(UUID employeeId) {
    return payrollRepo.findByEmployee_IdOrderByPayrollYearDescPayrollMonthDesc(employeeId)
          .stream().map(mapper::toSummary).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public PayrollResponse getMyPayrollForPeriod(UUID employeeId, int month, int year) {
    return payrollRepo.findByEmployee_IdAndPayrollMonthAndPayrollYear(employeeId, month, year)
          .map(mapper::toResponse)
          .orElseThrow(() -> new RuntimeException("Payroll not found for this period"));
  }

  @Override
  public void resendPayslip(UUID payrollId) {
    Payroll payroll = payrollRepo.findByIdWithDetails(payrollId)
          .orElseThrow(() -> new RuntimeException("Payroll not found"));
    sendPayslipSafely(payroll);
  }

  @Override
  public byte[] downloadPayslip(UUID payrollId) {
    Payroll payroll = payrollRepo.findByIdWithDetails(payrollId)
          .orElseThrow(() -> new RuntimeException("Payroll not found"));
    try {
      return payslipPdfService.generate(payroll);
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate payslip PDF", e);
    }
  }

  // ── Private helpers ────────────────────────────────────────────────────────

  private Payroll buildPayroll(Employee employee,
                               EmployeePayrollProfile profile,
                               int month,
                               int year,
                               Employee generatedBy) {

    // 1. Gross Pay
    BigDecimal grossPay = calculator.calculateGrossPay(
          profile.getBasicSalary(),
          profile.getHouseAllowance(),
          profile.getTransportAllowance(),
          profile.getMedicalAllowance(),
          profile.getOtherAllowance()
    );

    // 2. Statutory Deductions
    BigDecimal nssf = calculator.calculateEmployeeNssf(profile.getBasicSalary());
    BigDecimal employerNssf = calculator.calculateEmployerNssf(profile.getBasicSalary());

    BigDecimal shif = calculator.calculateEmployeeShif(grossPay);
    BigDecimal employerShif = calculator.calculateEmployerShif(grossPay);

    BigDecimal housingLevy = calculator.calculateHousingLevy(grossPay);

    // 3. Taxable Pay (after statutory deductions + pension)
    BigDecimal taxablePay = calculator.calculateTaxablePay(
          grossPay,
          nssf,
          shif,
          housingLevy,
          profile.getPensionContribution()
    );

    // 4. PAYE
    TaxCalculation tax = calculator.calculateTax(taxablePay);

    BigDecimal incomeTax = tax.incomeTax();
    BigDecimal personalRelief = tax.personalRelief();
    BigDecimal paye = tax.paye();

    // 5. Totals
    BigDecimal statutoryDeductions = nssf
          .add(shif)
          .add(housingLevy);

    BigDecimal payAfterStatutoryDeductions = grossPay.subtract(statutoryDeductions);

    BigDecimal totalDeductions = statutoryDeductions
          .add(profile.getPensionContribution())
          .add(paye);

    BigDecimal netPay = grossPay.subtract(totalDeductions);

    // 6. Build Payroll
    Payroll payroll = Payroll.builder()
          .employee(employee)
          .payrollMonth(month)
          .payrollYear(year)
          .payrollDate(LocalDate.now())
          .payrollNumber(generatePayrollNumber(employee.getEmployeeNumber(), month, year))

          .grossPay(grossPay)
          .taxablePay(taxablePay)

          .incomeTax(incomeTax)
          .personalRelief(personalRelief)
          .paye(paye)

          .totalEarnings(grossPay)

          .nssf(nssf)
          .employerNssf(employerNssf)

          .shif(shif)
          .employerShif(employerShif)

          .housingLevy(housingLevy)

          .statutoryDeductions(statutoryDeductions)
          .payAfterStatutoryDeductions(payAfterStatutoryDeductions)

          .totalDeductions(totalDeductions)
          .netPay(netPay)

          .status(PayrollStatus.GENERATED)
          .generatedBy(generatedBy)
          .build();

    // 7. Earnings
    List<PayrollEarning> earnings = new ArrayList<>();
    addEarning(earnings, payroll, "Basic Salary", profile.getBasicSalary());
    addEarning(earnings, payroll, "House Allowance", profile.getHouseAllowance());
    addEarning(earnings, payroll, "Transport Allowance", profile.getTransportAllowance());
    addEarning(earnings, payroll, "Medical Allowance", profile.getMedicalAllowance());
    addEarning(earnings, payroll, "Other Allowance", profile.getOtherAllowance());
    payroll.setEarnings(new HashSet<>(earnings));

    // 8. Deductions
    List<PayrollDeduction> deductions = new ArrayList<>();
    addDeduction(deductions, payroll, "PAYE", paye);
    addDeduction(deductions, payroll, "NSSF", nssf);
    addDeduction(deductions, payroll, "SHIF", shif);
    addDeduction(deductions, payroll, "Housing Levy", housingLevy);

    if (profile.getPensionContribution().compareTo(BigDecimal.ZERO) > 0) {
      addDeduction(deductions, payroll, "Pension", profile.getPensionContribution());
    }

    payroll.setDeductions(new HashSet<>(deductions));

    return payroll;
  }

  private void addEarning(List<PayrollEarning> list, Payroll payroll,
                          String typeName, BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return;
    EarningType type = earningTypeRepo.findByNameIgnoreCase(typeName)
          .orElseThrow(() -> new RuntimeException("EarningType not found: " + typeName));
    list.add(PayrollEarning.builder()
          .payroll(payroll)
          .earningType(type)
          .amount(amount)
          .build());
  }

  private void addDeduction(List<PayrollDeduction> list, Payroll payroll,
                            String typeName, BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return;
    DeductionType type = deductionTypeRepo.findByNameIgnoreCase(typeName)
          .orElseThrow(() -> new RuntimeException("DeductionType not found: " + typeName));
    list.add(PayrollDeduction.builder()
          .payroll(payroll)
          .deductionType(type)
          .amount(amount)
          .build());
  }

  private String generatePayrollNumber(String employeeNumber, int month, int year) {
    return "PAY-%d-%02d-%s".formatted(year, month, employeeNumber);
  }

  private String period(Payroll p) {
    return Month.of(p.getPayrollMonth())
          .getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + p.getPayrollYear();
  }

  private void sendPayslipSafely(Payroll payroll) {
    try {
      byte[] pdf = payslipPdfService.generate(payroll);
      Employee emp = payroll.getEmployee();
      emailService.sendPayslip(
            emp.getEmail(),
            emp.getFirstName() + " " + emp.getLastName(),
            period(payroll),
            payroll.getPayrollNumber(),
            emp.getEmployeeNumber(),
            payroll.getGrossPay(),
            payroll.getTotalDeductions(),
            payroll.getPaye(),
            payroll.getIncomeTax(),
            payroll.getPersonalRelief(),
            payroll.getNssf(),
            payroll.getShif(),
            payroll.getHousingLevy(),
            payroll.getNetPay(),
            payroll.getStatutoryDeductions(),
            payroll.getPayAfterStatutoryDeductions(),
            pdf);
    } catch (Exception e) {
      log.error("Payslip email failed for payroll {}: {}",
            payroll.getPayrollNumber(), e.getMessage());
    }
  }

  private Payroll getByIdOrThrow(UUID id) {
    return payrollRepo.findById(id)
          .orElseThrow(() -> new RuntimeException("Payroll not found: " + id));
  }

  private void assertStatus(Payroll payroll, PayrollStatus required, String action) {
    if (payroll.getStatus() != required) {
      throw new IllegalStateException(
            "Cannot %s payroll with status %s. Required: %s"
                  .formatted(action, payroll.getStatus(), required)
      );
    }
  }

}
