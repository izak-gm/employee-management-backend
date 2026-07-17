package com.riverbank.employee_management_backend.mapper;

import com.riverbank.employee_management_backend.dto.position.PositionRequest;
import com.riverbank.employee_management_backend.dto.position.PositionResponse;
import com.riverbank.employee_management_backend.entity.Position;

import org.springframework.stereotype.Component;

@Component
public class PositionMapper {

  public Position toEntity(PositionRequest request) {
    if (request == null) {
      return null;
    }
    return Position.builder()
          .name(request.name())
          .description(request.description())
          .active(true)
          .build();
  }

  public void updateEntity(Position position, PositionRequest request) {
    position.setName(request.name());
    position.setDescription(request.description());
  }

  public PositionResponse toResponse(Position position) {
    if (position == null) {
      return null;
    }
    return new PositionResponse(
          position.getId(),
          position.getName(),
          position.getDescription(),
          position.isActive()
    );
  }

}