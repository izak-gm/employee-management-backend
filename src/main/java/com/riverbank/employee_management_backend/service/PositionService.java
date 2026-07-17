package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.dto.position.PositionRequest;
import com.riverbank.employee_management_backend.dto.position.PositionResponse;

import java.util.List;
import java.util.UUID;

public interface PositionService {

  PositionResponse createPosition(PositionRequest request);

  List<PositionResponse> findPositionsAll();

  PositionResponse findPositionById(UUID id);

  PositionResponse updatePosition(UUID id, PositionRequest request);

  void deletePosition(UUID id);
}