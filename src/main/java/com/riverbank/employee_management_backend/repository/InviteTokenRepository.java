package com.riverbank.employee_management_backend.repository;

import com.riverbank.employee_management_backend.entity.InviteToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InviteTokenRepository extends JpaRepository<InviteToken, UUID> {
  Optional<InviteToken> findByToken(String token);

  void deleteByEmployeeId(UUID employeeId);
}