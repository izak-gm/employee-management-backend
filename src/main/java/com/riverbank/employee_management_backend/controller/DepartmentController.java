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
  public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
          .body(departmentService.create(request));
  }

  @GetMapping
  public ResponseEntity<List<DepartmentResponse>> findAll() {
    return ResponseEntity.ok(departmentService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<DepartmentResponse> findById(@PathVariable UUID id
  ) {
    return ResponseEntity.ok(departmentService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<DepartmentResponse> update(@PathVariable UUID id, @Valid @RequestBody DepartmentRequest request
  ) {
    return ResponseEntity.ok(departmentService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id
  ) {
    departmentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}