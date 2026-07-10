package com.riverbank.employee_management_backend.service.implementation;

import com.riverbank.employee_management_backend.dto.auth.EmployeeResponse;
import com.riverbank.employee_management_backend.dto.dashboard.DashboardStatsResponse;
import com.riverbank.employee_management_backend.dto.employee.LeaveActionRequest;
import com.riverbank.employee_management_backend.dto.employee.LeaveRequest;
import com.riverbank.employee_management_backend.dto.employee.LeaveResponse;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.entity.Leave;
import com.riverbank.employee_management_backend.enums.EmployeeStatus;
import com.riverbank.employee_management_backend.enums.LeaveStatus;
import com.riverbank.employee_management_backend.enums.Role;
import com.riverbank.employee_management_backend.exception.EmployeeNotFoundException;
import com.riverbank.employee_management_backend.repository.EmployeeRepository;
import com.riverbank.employee_management_backend.repository.LeaveRepository;
import com.riverbank.employee_management_backend.service.employee.EmployeeService;
import com.riverbank.employee_management_backend.util.EmployeeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final LeaveRepository leaveRepository;
  private final EmployeeUtils employeeUtils;


  // --- Leave management ---

  @Transactional
  public LeaveResponse applyForLeave(String employeeEmail, LeaveRequest request) {
    Employee employee = employeeRepository.findByEmail(employeeEmail)
          .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));

    Employee coverEmployee = null;
    if (request.coverEmployeeId() != null) {
      coverEmployee = employeeRepository.findById(request.coverEmployeeId())
            .orElseThrow(() -> new EmployeeNotFoundException("Cover employee not found"));
      if (coverEmployee.getStatus() != EmployeeStatus.ACTIVE) {
        throw new RuntimeException("Cover employee must be active");
      }
    }

    Leave leave = Leave.builder()
          .employee(employee)
          .coverEmployee(coverEmployee)
          .leaveType(request.leaveType())
          .startDate(request.startDate())
          .endDate(request.endDate())
          .reason(request.reason())
          .status(LeaveStatus.PENDING)
          .build();

    return toLeaveResponse(leaveRepository.save(leave));
  }

  @Transactional
  public LeaveResponse actionLeave(UUID leaveId, LeaveActionRequest request, String adminEmail) {
    Leave leave = leaveRepository.findById(leaveId)
          .orElseThrow(() -> new RuntimeException("Leave request not found"));

    Employee admin = employeeRepository.findByEmail(adminEmail)
          .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

    leave.setStatus(request.status());
    leave.setApprovedBy(admin);

    return toLeaveResponse(leaveRepository.save(leave));
  }

  public List<LeaveResponse> getMyLeaves(String employeeEmail) {
    Employee employee = employeeRepository.findByEmail(employeeEmail)
          .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));
    return leaveRepository.findByEmployeeIdOrderByCreatedAtDesc(employee.getId())
          .stream().map(this::toLeaveResponse).toList();
  }

  public List<LeaveResponse> getAllLeaves() {
    return leaveRepository.findAll().stream().map(this::toLeaveResponse).toList();
  }

  public List<LeaveResponse> getPendingLeaves() {
    return leaveRepository.findByStatus(LeaveStatus.PENDING)
          .stream().map(this::toLeaveResponse).toList();
  }

  public List<EmployeeResponse> getActiveEmployees() {
    return employeeRepository.findByStatus(EmployeeStatus.ACTIVE)
          .stream().map(employeeUtils::toEmployeeResponse).toList();
  }

  // --- Dashboard stats ---

  public DashboardStatsResponse getDashboardStats() {
    return new DashboardStatsResponse(
          employeeRepository.count(),
          employeeRepository.countByStatus(EmployeeStatus.ACTIVE),
          employeeRepository.countByStatus(EmployeeStatus.INVITED),
          employeeRepository.countByStatus(EmployeeStatus.INACTIVE),
          employeeRepository.countByRole(Role.ADMIN),
          employeeRepository.countByRole(Role.SUPERADMIN),
          leaveRepository.countByStatus(LeaveStatus.PENDING),
          leaveRepository.countByStatus(LeaveStatus.APPROVED),
          leaveRepository.countByStatus(LeaveStatus.REJECTED),
          leaveRepository.count()
    );
  }

  // --- Mappers ---


  private LeaveResponse toLeaveResponse(Leave l) {
    return new LeaveResponse(
          l.getId(),
          l.getEmployee().getId(),
          l.getEmployee().getFirstName() + " " + l.getEmployee().getLastName(),
          l.getCoverEmployee() != null ? l.getCoverEmployee().getId() : null,
          l.getCoverEmployee() != null ? l.getCoverEmployee().getFirstName() + " " + l.getCoverEmployee().getLastName() : null,
          l.getLeaveType(),
          l.getStatus(),
          l.getStartDate(),
          l.getEndDate(),
          l.getReason(),
          l.getApprovedBy() != null ? l.getApprovedBy().getFirstName() + " " + l.getApprovedBy().getLastName() : null,
          l.getCreatedAt()
    );
  }
}