package com.riverbank.employee_management_backend.service.implementation;

import com.riverbank.employee_management_backend.dto.department.DepartmentRequest;
import com.riverbank.employee_management_backend.dto.department.DepartmentResponse;
import com.riverbank.employee_management_backend.entity.Department;
import com.riverbank.employee_management_backend.exception.ResourceAlreadyExistsException;
import com.riverbank.employee_management_backend.exception.ResourceNotFoundException;
import com.riverbank.employee_management_backend.mapper.DepartmentMapper;
import com.riverbank.employee_management_backend.repository.DepartmentRepository;
import com.riverbank.employee_management_backend.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
  private final DepartmentRepository repository;
  private final DepartmentMapper mapper;

  @Override
  public DepartmentResponse create(DepartmentRequest request) {
    if (repository.existsByName(request.name()))
      throw new ResourceAlreadyExistsException("Department already exists");

    return mapper.toResponse(repository.save(mapper.toEntity(request)));
  }

  @Override
  public List<DepartmentResponse> findAll() {
    return repository.findAll()
          .stream()
          .map(mapper::toResponse)
          .toList();
  }

  @Override
  public DepartmentResponse findById(UUID id) {
    return mapper.toResponse(getDepartment(id));
  }

  @Override
  public DepartmentResponse update(UUID id, DepartmentRequest request) {
    Department department = getDepartment(id);
    mapper.updateEntity(department, request);
    return mapper.toResponse(repository.save(department));
  }

  @Override
  public void delete(UUID id) {
    Department department = getDepartment(id);
    department.setActive(false);
    repository.save(department);
  }

  private Department getDepartment(UUID id) {
    return repository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
  }
}