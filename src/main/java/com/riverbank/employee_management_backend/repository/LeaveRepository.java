package com.riverbank.employee_management_backend.repository;


import com.riverbank.employee_management_backend.entity.Leave;
import com.riverbank.employee_management_backend.enums.LeaveStatus;
import com.riverbank.employee_management_backend.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LeaveRepository extends JpaRepository<Leave, UUID> {
  List<Leave> findByEmployeeId(UUID employeeId);

  List<Leave> findByStatus(LeaveStatus status);

  long countByStatus(LeaveStatus status);

  List<Leave> findByCoverEmployeeIdAndStatus(UUID coverEmployeeId, LeaveStatus status);

  List<Leave> findByEmployeeIdAndLeaveTypeAndStatusInAndStartDateBetween(
        UUID employeeId, LeaveType leaveType, List<LeaveStatus> statuses, LocalDate from, LocalDate to);

  @Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId ORDER BY l.createdAt DESC")
  List<Leave> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);

  @Query("""
        SELECT l FROM Leave l
        WHERE l.employee.id = :employeeId
        AND l.status IN :statuses
        AND l.startDate <= :endDate
        AND l.endDate >= :startDate
        """)
  List<Leave> findOverlappingLeaves(
        @Param("employeeId") UUID employeeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("statuses") List<LeaveStatus> statuses
  );
}