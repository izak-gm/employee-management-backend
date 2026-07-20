package com.riverbank.employee_management_backend.mapper;

import com.riverbank.employee_management_backend.dto.payroll.DeductionTypeResponse;
import com.riverbank.employee_management_backend.dto.payroll.EarningTypeResponse;
import com.riverbank.employee_management_backend.entity.payrolls.DeductionType;
import com.riverbank.employee_management_backend.entity.payrolls.EarningType;
import org.springframework.stereotype.Component;

@Component
public class EarningDeductionMapper {

  public EarningTypeResponse toResponse(EarningType e) {
    return new EarningTypeResponse(
          e.getId(),
          e.getName(),
          e.getDescription(),
          e.isTaxable(),
          e.isFixed(),
          e.isActive(),
          e.getDisplayOrder()
    );
  }

  public DeductionTypeResponse toResponse(DeductionType d) {
    return new DeductionTypeResponse(
          d.getId(),
          d.getName(),
          d.getDescription(),
          d.isStatutory(),
          d.isTaxable(),
          d.isActive(),
          d.getCalculationType(),
          d.getFixedAmount(),
          d.getPercentage(),
          d.getDisplayOrder()
    );
  }
}