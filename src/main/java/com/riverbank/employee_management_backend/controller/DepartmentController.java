package com.riverbank.employee_management_backend.controller;

import com.riverbank.employee_management_backend.dto.department.DepartmentRequest;
import com.riverbank.employee_management_backend.dto.department.DepartmentResponse;
import com.riverbank.employee_management_backend.service.DepartmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Department")
public class DepartmentController {
  private final DepartmentService departmentService;

  @PostMapping
  public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
          .body(departmentService.createDepartment(request));
  }

  @GetMapping
  public ResponseEntity<List<DepartmentResponse>> findAllDepartment() {
    return ResponseEntity.ok(departmentService.findDepartmentsAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<DepartmentResponse> findDepartmentById(@PathVariable UUID id
  ) {
    return ResponseEntity.ok(departmentService.findDepartmentById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable UUID id, @Valid @RequestBody DepartmentRequest request
  ) {
    return ResponseEntity.ok(departmentService.updateDepartment(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDepartment(@PathVariable UUID id
  ) {
    departmentService.deleteDepartment(id);
    return ResponseEntity.noContent().build();
  }
}