package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.position.PositionRequest;
import com.riverbank.employee_management_backend.dto.position.PositionResponse;

import java.util.List;
import java.util.UUID;

public interface PositionService {

  PositionResponse create(PositionRequest request);

  List<PositionResponse> findAll();

  PositionResponse findById(UUID id);

  PositionResponse update(UUID id, PositionRequest request);

  void delete(UUID id);
}