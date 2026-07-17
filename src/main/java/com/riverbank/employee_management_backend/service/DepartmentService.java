package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.department.DepartmentRequest;
import com.riverbank.employee_management_backend.dto.department.DepartmentResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface DepartmentService {

  DepartmentResponse createDepartment(DepartmentRequest request);

  List<DepartmentResponse> findDepartmentsAll();

  DepartmentResponse findDepartmentById(UUID id);

  DepartmentResponse updateDepartment(UUID id, DepartmentRequest request);

  void deleteDepartment(UUID id);
}