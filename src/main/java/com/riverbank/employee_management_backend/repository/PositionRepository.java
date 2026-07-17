package com.riverbank.employee_management_backend.repository;

import com.riverbank.employee_management_backend.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {
}
