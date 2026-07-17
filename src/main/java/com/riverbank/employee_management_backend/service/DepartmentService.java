package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.department.DepartmentRequest;
import com.riverbank.employee_management_backend.dto.department.DepartmentResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface DepartmentService {

  DepartmentResponse create(DepartmentRequest request);

  List<DepartmentResponse> findAll();

  DepartmentResponse findById(UUID id);

  DepartmentResponse update(UUID id, DepartmentRequest request);

  void delete(UUID id);
}