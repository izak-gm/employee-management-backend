package com.riverbank.employee_management_backend.util;

import com.riverbank.employee_management_backend.dto.payroll.PAYE.TaxCalculation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Kenyan statutory payroll calculations.
 * All figures are monthly. Rates current as of 2026.
 * Keep this class pure — no DB calls, no Spring dependencies beyond @Component.
 */
@Component
public class PayrollCalculator {

  private static final RoundingMode ROUND = RoundingMode.HALF_UP;

  // ── NSSF (New Rates — Feb 2026) ────────────────────────────────────────────
  // Tier I : 6% on first KES 7,000  (max KES 420)
  // Tier II: 6% on next  KES 29,000 (max KES 1,740)
  // Employee max = KES 2,160 | Employer matches exactly
  // NSSF_Rate is 6% across all tier

  private static final BigDecimal NSSF_TIER1_LIMIT = new BigDecimal("9000");
  private static final BigDecimal NSSF_TIER2_LIMIT = new BigDecimal("99000");
  private static final BigDecimal NSSF_RATE = new BigDecimal("0.06");
  // ── Housing Levy ───────────────────────────────────────────────────────────
  // 1.5% of gross pay — employee deduction only
  private static final BigDecimal HOUSING_LEVY_RATE = new BigDecimal("0.015");

  // ── SHIF (Social Health Insurance Fund) ────────────────────────────────────
  // 2.75% of gross pay — no cap, no minimum
  // Employer matches employee contribution
  private static final BigDecimal SHIF_RATE = new BigDecimal("0.0275");

  // ── PAYE (KRA 2024 Monthly Bands) ──────────────────────────────────────────
  // Band 1 : 0      – 24,000  @ 10%
  // Band 2 : 24,001 – 32,333  @ 25%
  // Band 3 : 32,334 – 500,000 @ 30%
  // Band 4 : 500,001 – 800,000 @ 32.5%
  // Band 5 : 800,001+          @ 35%
  // Personal relief: KES 2,400/month
  // Insurance relief: 15% of premiums paid (if applicable — skipped for now)
  private static final BigDecimal PERSONAL_RELIEF = new BigDecimal("2400");
  private static final BigDecimal[][] PAYE_BANDS = {
        {new BigDecimal("24000"), new BigDecimal("0.10")},
        {new BigDecimal("8333"), new BigDecimal("0.25")},
        {new BigDecimal("467667"), new BigDecimal("0.30")},
        {new BigDecimal("300000"), new BigDecimal("0.325")},
        {null, new BigDecimal("0.35")}, // null = no upper limit
  };

  public BigDecimal calculateEmployeeNssf(BigDecimal basicSalary) {
    BigDecimal tier1 = basicSalary.min(NSSF_TIER1_LIMIT)
          .multiply(NSSF_RATE).setScale(2, ROUND);

    BigDecimal tier2 = basicSalary.subtract(NSSF_TIER1_LIMIT)
          .max(BigDecimal.ZERO)
          .min(NSSF_TIER2_LIMIT)
          .multiply(NSSF_RATE).setScale(2, ROUND);

    return tier1.add(tier2);
  }


  public BigDecimal calculateEmployerNssf(BigDecimal basicSalary) {
    // Employer matches employee contribution
    return calculateEmployeeNssf(basicSalary);
  }

  public BigDecimal calculateEmployeeShif(BigDecimal grossPay) {
    return grossPay.multiply(SHIF_RATE).setScale(2, ROUND);
  }

  // ── Taxable Pay ────────────────────────────────────────────────────────────
  // Gross - NSSF - Pension contribution (if any)

  public BigDecimal calculateEmployerShif(BigDecimal grossPay) {
    return calculateEmployeeShif(grossPay);
  }


  public BigDecimal calculateHousingLevy(BigDecimal grossPay) {
    return grossPay.multiply(HOUSING_LEVY_RATE).setScale(2, ROUND);
  }

  public BigDecimal calculateTaxablePay(BigDecimal grossPay,
                                        BigDecimal nssf,
                                        BigDecimal pensionContribution) {
    return grossPay
          .subtract(nssf)
          .subtract(pensionContribution)
          .max(BigDecimal.ZERO);
  }

  public BigDecimal calculateIncomeTax(BigDecimal taxablePay) {
    BigDecimal tax = BigDecimal.ZERO;
    BigDecimal remaining = taxablePay;

    for (BigDecimal[] band : PAYE_BANDS) {
      if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

      BigDecimal bandSize = (band[0] != null)
            ? band[0].min(remaining)
            : remaining;

      tax = tax.add(bandSize.multiply(band[1]).setScale(2, ROUND));
      remaining = remaining.subtract(bandSize);
    }

    // This returns the whole income tax
    return tax.setScale(2, ROUND);
  }

  // ── Gross Pay ──────────────────────────────────────────────────────────────
  public BigDecimal calculateGrossPay(BigDecimal basicSalary,
                                      BigDecimal houseAllowance,
                                      BigDecimal transportAllowance,
                                      BigDecimal medicalAllowance,
                                      BigDecimal otherAllowance) {
    return basicSalary
          .add(houseAllowance)
          .add(transportAllowance)
          .add(medicalAllowance)
          .add(otherAllowance);
  }

  public TaxCalculation calculateTax(BigDecimal taxablePay) {
    BigDecimal incomeTax = calculateIncomeTax(taxablePay);

    BigDecimal paye = incomeTax
          .subtract(PERSONAL_RELIEF)
          .max(BigDecimal.ZERO)
          .setScale(2, ROUND);

    return new TaxCalculation(
          incomeTax,
          PERSONAL_RELIEF,
          paye
    );
  }
  
  public BigDecimal getPersonalRelief() {
    return PERSONAL_RELIEF;
  }
}
