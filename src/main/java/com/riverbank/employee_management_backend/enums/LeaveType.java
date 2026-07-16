package com.riverbank.employee_management_backend.enums;

import com.riverbank.employee_management_backend.enus.Gender;

public enum LeaveType {

  ANNUAL(21, false, null),
  SICK(7, false, null),
  PATERNITY(14, true, Gender.MALE),
  MATERNITY(90, true, Gender.FEMALE),
  COMPASSIONATE(-1, false, null);

  private final int maxDays;
  private final boolean requiresFullBlock;
  private final Gender eligibleGender;

  LeaveType(int maxDays, boolean requiresFullBlock, Gender eligibleGender) {
    this.maxDays = maxDays;
    this.requiresFullBlock = requiresFullBlock;
    this.eligibleGender = eligibleGender;
  }

  public int getMaxDays() {
    return maxDays;
  }

  public boolean isUnlimited() {
    return maxDays == -1;
  }

  public boolean requiresFullBlock() {
    return requiresFullBlock;
  }

  public Gender getEligibleGender() {
    return eligibleGender;
  }

  public boolean isEligible(Gender gender) {
    return eligibleGender == null || eligibleGender == gender;
  }
}