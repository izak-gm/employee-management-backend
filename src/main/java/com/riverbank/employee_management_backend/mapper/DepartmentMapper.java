package com.riverbank.employee_management_backend.mapper;

import com.riverbank.employee_management_backend.dto.department.DepartmentRequest;
import com.riverbank.employee_management_backend.dto.department.DepartmentResponse;
import com.riverbank.employee_management_backend.entity.Department;

import org.springframework.stereotype.Component;


@Component
public class DepartmentMapper {


  public Department toEntity(DepartmentRequest request) {

    if (request == null) {
      return null;
    }


    return Department.builder()
          .name(request.name())
          .description(request.description())
          .active(true)
          .build();
  }


  public void updateEntity(
        Department department,
        DepartmentRequest request
  ) {

    department.setName(request.name());
    department.setDescription(request.description());
  }


  public DepartmentResponse toResponse(
        Department department
  ) {

    if (department == null) {
      return null;
    }


    return new DepartmentResponse(
          department.getId(),
          department.getName(),
          department.getDescription(),
          department.isActive()
    );
  }

}