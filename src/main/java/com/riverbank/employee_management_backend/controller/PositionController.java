package com.riverbank.employee_management_backend.controller;

import com.riverbank.employee_management_backend.dto.position.PositionRequest;
import com.riverbank.employee_management_backend.dto.position.PositionResponse;
import com.riverbank.employee_management_backend.service.PositionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
@Tag(name = "Position")
public class PositionController {
  private final PositionService service;

  @PostMapping
  public ResponseEntity<PositionResponse> create(@Valid @RequestBody PositionRequest request) {

    return ResponseEntity.status(HttpStatus.CREATED)
          .body(service.create(request));
  }

  @GetMapping
  public ResponseEntity<List<PositionResponse>> findAll() {

    return ResponseEntity.ok(service.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<PositionResponse> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(service.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PositionResponse> update(@PathVariable UUID id, @Valid @RequestBody PositionRequest request) {

    return ResponseEntity.ok(service.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}