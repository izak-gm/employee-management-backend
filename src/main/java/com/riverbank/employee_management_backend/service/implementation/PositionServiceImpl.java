package com.riverbank.employee_management_backend.service.implementation;

import com.riverbank.employee_management_backend.dto.position.PositionRequest;
import com.riverbank.employee_management_backend.dto.position.PositionResponse;
import com.riverbank.employee_management_backend.entity.Position;
import com.riverbank.employee_management_backend.mapper.PositionMapper;
import com.riverbank.employee_management_backend.repository.PositionRepository;
import com.riverbank.employee_management_backend.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {
  private final PositionRepository repository;
  private final PositionMapper mapper;

  @Override
  public PositionResponse create(PositionRequest request) {
    if (repository.existsByName(request.name()))
      throw new RuntimeException("Position already exists");
    return mapper.toResponse(
          repository.save(mapper.toEntity(request))
    );
  }

  @Override
  public List<PositionResponse> findAll() {
    return repository.findAll()
          .stream()
          .map(mapper::toResponse)
          .toList();
  }

  @Override
  public PositionResponse findById(UUID id) {

    return mapper.toResponse(getPosition(id));
  }

  @Override
  public PositionResponse update(UUID id, PositionRequest request) {
    Position position = getPosition(id);
    mapper.updateEntity(position, request);
    return mapper.toResponse(repository.save(position));
  }

  @Override
  public void delete(UUID id) {
    Position position = getPosition(id);
    position.setActive(false);
    repository.save(position);
  }

  private Position getPosition(UUID id) {
    return repository.findById(id)
          .orElseThrow(
                () -> new RuntimeException("Position not found")
          );
  }
}