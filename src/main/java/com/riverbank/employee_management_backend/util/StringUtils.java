package com.riverbank.employee_management_backend.util;

public final class StringUtils {

  private StringUtils() {
    // prevent instantiation — utility class
  }

  public static String safe(String value) {
    return value == null ? "" : value;
  }
}