package com.riverbank.employee_management_backend.util;

import com.riverbank.employee_management_backend.dto.auth.EmployeeResponse;
import com.riverbank.employee_management_backend.entity.Employee;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class EmployeeUtils {
  /**
   * Splits a date range into the number of days falling within each calendar year it touches.
   * e.g. Dec 25 2026 - Jan 10 2027 -> {2026=7, 2027=10}
   */
  public static Map<Integer, Integer> splitDaysByYear(LocalDate startDate, LocalDate endDate) {
    Map<Integer, Integer> daysPerYear = new LinkedHashMap<>();
    LocalDate cursor = startDate;

    while (!cursor.isAfter(endDate)) {
      LocalDate yearEnd = LocalDate.of(cursor.getYear(), 12, 31);
      LocalDate segmentEnd = yearEnd.isBefore(endDate) ? yearEnd : endDate;
      int daysInThisYear = (int) ChronoUnit.DAYS.between(cursor, segmentEnd) + 1;
      daysPerYear.merge(cursor.getYear(), daysInThisYear, Integer::sum);
      cursor = segmentEnd.plusDays(1);
    }

    return daysPerYear;
  }

  public EmployeeResponse toEmployeeResponse(Employee e) {
    return new EmployeeResponse(e.getId(), e.getFirstName(), e.getLastName(),
          e.getEmail(), e.getPhoneNumber(), e.getRole(), e.getGender());
  }
}
