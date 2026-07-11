package com.riverbank.employee_management_backend.enums;

public enum LeaveType {
  ANNUAL(21, false),
  SICK(7, false),
  PATERNITY(14, true),
  MATERNITY(90, true),
  COMPASSIONATE(-1, false); // -1 = unlimited

  private final int maxDays;
  private final boolean requiresFullBlock; // must be taken as one continuous block of exactly maxDays

  LeaveType(int maxDays, boolean requiresFullBlock) {
    this.maxDays = maxDays;
    this.requiresFullBlock = requiresFullBlock;
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
}