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
  private final PositionService positionService;

  @PostMapping
  public ResponseEntity<PositionResponse> createPosition(@Valid @RequestBody PositionRequest request) {

    return ResponseEntity.status(HttpStatus.CREATED)
          .body(positionService.createPosition(request));
  }

  @GetMapping
  public ResponseEntity<List<PositionResponse>> findPositionsAll() {

    return ResponseEntity.ok(positionService.findPositionsAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<PositionResponse> findPositionById(@PathVariable UUID id) {
    return ResponseEntity.ok(positionService.findPositionById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PositionResponse> updatePosition(@PathVariable UUID id, @Valid @RequestBody PositionRequest request) {

    return ResponseEntity.ok(positionService.updatePosition(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePosition(@PathVariable UUID id) {
    positionService.deletePosition(id);
    return ResponseEntity.noContent().build();
  }
}