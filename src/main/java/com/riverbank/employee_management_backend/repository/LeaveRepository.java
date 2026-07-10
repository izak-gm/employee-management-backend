package com.riverbank.employee_management_backend.repository;


import com.riverbank.employee_management_backend.entity.Leave;
import com.riverbank.employee_management_backend.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LeaveRepository extends JpaRepository<Leave, UUID> {
  List<Leave> findByEmployeeId(UUID employeeId);

  List<Leave> findByStatus(LeaveStatus status);

  long countByStatus(LeaveStatus status);

  @Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId ORDER BY l.createdAt DESC")
  List<Leave> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);
}