package com.riverbank.employee_management_backend.service.implementation;

import com.riverbank.employee_management_backend.dto.auth.EmployeeResponse;
import com.riverbank.employee_management_backend.dto.dashboard.DashboardStatsResponse;
import com.riverbank.employee_management_backend.dto.employee.LeaveActionRequest;
import com.riverbank.employee_management_backend.dto.employee.LeaveRequest;
import com.riverbank.employee_management_backend.dto.employee.*;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.entity.Leave;
import com.riverbank.employee_management_backend.enums.*;
import com.riverbank.employee_management_backend.exception.EmployeeNotFoundException;
import com.riverbank.employee_management_backend.exception.LeaveActionNotAllowedException;
import com.riverbank.employee_management_backend.repository.EmployeeRepository;
import com.riverbank.employee_management_backend.repository.LeaveRepository;
import com.riverbank.employee_management_backend.service.employee.EmployeeService;
import com.riverbank.employee_management_backend.util.EmployeeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
  private static final List<LeaveStatus> ACTIVE_LEAVE_STATUSES = List.of(
        LeaveStatus.PENDING_COVER,
        LeaveStatus.COVER_DECLINED,
        LeaveStatus.PENDING_ADMIN,
        LeaveStatus.APPROVED
  );
  private final EmployeeRepository employeeRepository;
  private final LeaveRepository leaveRepository;
  private final EmployeeUtils employeeUtils;

  // --- Leave management ---
  @Transactional
  public LeaveResponse applyForLeave(String employeeEmail, LeaveRequest request) {
    Employee employee = employeeRepository.findByEmail(employeeEmail)
          .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));

    if (request.leaveType() != LeaveType.COMPASSIONATE
          && request.coverEmployeeId() == null) {
      throw new LeaveActionNotAllowedException(
            "Please select a cover employee before submitting your leave request.");
    }
    LocalDate today = LocalDate.now();

    if (!request.startDate().isAfter(today)) {
      throw new LeaveActionNotAllowedException(
            "Leave must start from tomorrow onwards."
      );
    }

    validateNoOverlappingLeave(employee.getId(), request.startDate(), request.endDate());
    validateLeaveBalance(employee, request);

    Employee coverEmployee = null;
    if (request.coverEmployeeId() != null) {
      if (request.coverEmployeeId().equals(employee.getId())) {
        throw new LeaveActionNotAllowedException("You cannot assign yourself as cover employee");
      }
      coverEmployee = employeeRepository.findById(request.coverEmployeeId())
            .orElseThrow(() -> new EmployeeNotFoundException("Cover employee not found"));
      if (coverEmployee.getStatus() != EmployeeStatus.ACTIVE) {
        throw new RuntimeException("Cover employee must be active");
      }
      validateCoverEmployeeAvailable(coverEmployee.getId(), request.startDate(), request.endDate());
    }

    Leave leave = Leave.builder()
          .employee(employee)
          .coverEmployee(coverEmployee)
          .leaveType(request.leaveType())
          .startDate(request.startDate())
          .endDate(request.endDate())
          .reason(request.reason())
          .status(coverEmployee != null ? LeaveStatus.PENDING_COVER : LeaveStatus.PENDING_ADMIN)
          .build();
    return toLeaveResponse(leaveRepository.save(leave));
  }

  @Transactional
  public LeaveResponse updateLeave(UUID leaveId, String employeeEmail, LeaveRequest request) {
    Leave leave = getOwnedLeave(leaveId, employeeEmail);
    if (leave.getStatus() != LeaveStatus.PENDING_COVER && leave.getStatus() != LeaveStatus.COVER_DECLINED) {
      throw new LeaveActionNotAllowedException("Cannot edit a leave once your cover has accepted it");
    }

    validateNoOverlappingLeave(leave.getEmployee().getId(), request.startDate(), request.endDate(), leaveId);
    validateLeaveBalance(leave.getEmployee(), request, leaveId);

    leave.setLeaveType(request.leaveType());
    leave.setStartDate(request.startDate());
    leave.setEndDate(request.endDate());
    leave.setReason(request.reason());
    if (request.coverEmployeeId() != null) {
      if (request.coverEmployeeId().equals(leave.getEmployee().getId())) {
        throw new LeaveActionNotAllowedException("You cannot assign yourself as cover employee");
      }
      Employee cover = employeeRepository.findById(request.coverEmployeeId())
            .orElseThrow(() -> new EmployeeNotFoundException("Cover employee not found"));
      validateCoverEmployeeAvailable(cover.getId(), request.startDate(), request.endDate());
      leave.setCoverEmployee(cover);
      if (leave.getStatus() == LeaveStatus.COVER_DECLINED) {
        leave.setStatus(LeaveStatus.PENDING_COVER);
      }
    }
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

  @Transactional
  public void deleteLeave(UUID leaveId, String employeeEmail) {
    Leave leave = getOwnedLeave(leaveId, employeeEmail);
    if (leave.getStatus() != LeaveStatus.PENDING_COVER && leave.getStatus() != LeaveStatus.COVER_DECLINED) {
      throw new LeaveActionNotAllowedException("Cannot delete a leave once your cover has accepted it");
    }
    leaveRepository.delete(leave);
  }

  @Transactional
  public LeaveResponse actionAsCover(UUID leaveId, CoverActionRequest request, String coverEmail) {
    Leave leave = leaveRepository.findById(leaveId)
          .orElseThrow(() -> new RuntimeException("Leave request not found"));
    if (leave.getCoverEmployee() == null || !leave.getCoverEmployee().getEmail().equals(coverEmail)) {
      throw new RuntimeException("You are not assigned as cover for this leave");
    }
    leave.setStatus(request.accept() ? LeaveStatus.PENDING_ADMIN : LeaveStatus.COVER_DECLINED);
    return toLeaveResponse(leaveRepository.save(leave));
  }

  @Transactional
  public LeaveResponse withdrawLeave(UUID leaveId, String employeeEmail) {
    Leave leave = getOwnedLeave(leaveId, employeeEmail);

    if (leave.getStatus() == LeaveStatus.REJECTED || leave.getStatus() == LeaveStatus.WITHDRAWN) {
      throw new LeaveActionNotAllowedException("This leave cannot be withdrawn");
    }
    if (!leave.getStartDate().isAfter(LocalDate.now())) {
      throw new LeaveActionNotAllowedException("Cannot withdraw a leave that has already started");
    }

    leave.setStatus(LeaveStatus.WITHDRAWN);
    return toLeaveResponse(leaveRepository.save(leave));
  }

  @Override
  @Transactional(readOnly = true)
  public LeaveResponse getLeaveById(UUID leaveId) {

    Leave leave = leaveRepository.findById(leaveId)
          .orElseThrow(() ->
                new LeaveActionNotAllowedException("Leave not found."));

    return toLeaveResponse(leave);
  }

  // Validations
  private void validateLeaveBalance(Employee employee, LeaveRequest request) {
    validateLeaveBalance(employee, request, null);
  }

  private void validateLeaveBalance(Employee employee, LeaveRequest request, UUID excludeLeaveId) {
    LeaveType type = request.leaveType();
    if (type.isUnlimited()) return;

    int totalRequestedDays = (int) ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;

    // Paternity/Maternity: must still be one continuous block of exactly maxDays overall,
    // even if it straddles a year boundary
    if (type.requiresFullBlock() && totalRequestedDays != type.getMaxDays()) {
      throw new LeaveActionNotAllowedException(
            String.format("%s leave must be taken as a single continuous block of exactly %d days. You requested %d day(s).",
                  type, type.getMaxDays(), totalRequestedDays));
    }

    // Check balance per calendar year the request touches
    Map<Integer, Integer> requestedDaysByYear = EmployeeUtils.splitDaysByYear(request.startDate(), request.endDate());

    for (Map.Entry<Integer, Integer> entry : requestedDaysByYear.entrySet()) {
      int year = entry.getKey();
      int requestedInYear = entry.getValue();

      int usedDays = getUsedDays(employee.getId(), type, year, excludeLeaveId);
      if (usedDays + requestedInYear > type.getMaxDays()) {
        if (type.requiresFullBlock()) {
          throw new LeaveActionNotAllowedException(
                String.format("You have already used your %s leave allocation for %d.", type, year));
        }
        throw new LeaveActionNotAllowedException(
              String.format("%s leave balance exceeded for %d. Used: %d, Requested in %d: %d, Limit: %d days/year",
                    type, year, usedDays, year, requestedInYear, type.getMaxDays()));
      }
    }
  }

  private void validateNoOverlappingLeave(UUID employeeId, LocalDate startDate, LocalDate endDate) {
    validateNoOverlappingLeave(employeeId, startDate, endDate, null);
  }

  private void validateNoOverlappingLeave(UUID employeeId, LocalDate startDate, LocalDate endDate, UUID excludeLeaveId) {
    boolean hasOverlap = leaveRepository
          .findOverlappingLeaves(employeeId, startDate, endDate, ACTIVE_LEAVE_STATUSES)
          .stream()
          .anyMatch(l -> !l.getId().equals(excludeLeaveId));

    if (hasOverlap) {
      throw new LeaveActionNotAllowedException(
            "You already have a leave request covering part of this period. " +
                  "Please choose dates that don't overlap with an existing leave."
      );
    }
  }

  private void validateCoverEmployeeAvailable(UUID coverEmployeeId, LocalDate startDate, LocalDate endDate) {
    boolean coverIsOnLeave = leaveRepository
          .findOverlappingLeaves(coverEmployeeId, startDate, endDate, ACTIVE_LEAVE_STATUSES)
          .stream()
          .findAny()
          .isPresent();

    if (coverIsOnLeave) {
      throw new LeaveActionNotAllowedException(
            "The selected cover employee has an overlapping leave request during this period. " +
                  "Please choose a different cover employee or adjust the dates."
      );
    }
  }

  // get Requests start here
  private int getUsedDays(UUID employeeId, LeaveType type, int year) {
    return getUsedDays(employeeId, type, year, null);
  }

  private int getUsedDays(UUID employeeId, LeaveType type, int year, UUID excludeLeaveId) {
    return leaveRepository.findByEmployeeIdAndLeaveTypeAndStatusInAndStartDateBetween(
                employeeId, type,
                List.of(LeaveStatus.APPROVED, LeaveStatus.PENDING_COVER, LeaveStatus.PENDING_ADMIN),
                LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31))
          .stream()
          .filter(l -> !l.getId().equals(excludeLeaveId))
          .mapToInt(l -> (int) ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate()) + 1)
          .sum();
  }

  // --- Notifications — derived, no new table ---

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
    return leaveRepository.findByStatus(LeaveStatus.PENDING_COVER)
          .stream().map(this::toLeaveResponse).toList();
  }

  public List<EmployeeResponse> getActiveEmployees() {
    return employeeRepository.findByStatus(EmployeeStatus.ACTIVE)
          .stream().map(employeeUtils::toEmployeeResponse).toList();
  }

  public List<LeaveResponse> getPendingCoverActionsForMe(String email) {
    Employee me = employeeRepository.findByEmail(email).orElseThrow();
    return leaveRepository.findByCoverEmployeeIdAndStatus(me.getId(), LeaveStatus.PENDING_COVER)
          .stream().map(this::toLeaveResponse).toList();
  }

  private Leave getOwnedLeave(UUID leaveId, String email) {
    Leave leave = leaveRepository.findById(leaveId).orElseThrow(() -> new RuntimeException("Leave not found"));
    if (!leave.getEmployee().getEmail().equals(email)) throw new RuntimeException("Not your leave request");
    return leave;
  }

  public List<LeaveBalanceResponse> getMyLeaveBalances(String email) {
    Employee employee = employeeRepository.findByEmail(email).orElseThrow();
    int year = LocalDate.now().getYear();
    return Arrays.stream(LeaveType.values()).map(type -> {
      if (type.isUnlimited()) return new LeaveBalanceResponse(type, -1, 0, -1, true);
      int used = getUsedDays(employee.getId(), type, year);
      return new LeaveBalanceResponse(type, type.getMaxDays(), used, type.getMaxDays() - used, false);
    }).toList();
  }

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

  // --- Dashboard stats ---

  public DashboardStatsResponse getDashboardStats() {
    return new DashboardStatsResponse(
          employeeRepository.count(),
          employeeRepository.countByStatus(EmployeeStatus.ACTIVE),
          employeeRepository.countByStatus(EmployeeStatus.INVITED),
          employeeRepository.countByStatus(EmployeeStatus.INACTIVE),
          employeeRepository.countByRole(Role.ADMIN),
          employeeRepository.countByRole(Role.SUPERADMIN),
          leaveRepository.countByStatus(LeaveStatus.PENDING_COVER),
          leaveRepository.countByStatus(LeaveStatus.APPROVED),
          leaveRepository.countByStatus(LeaveStatus.REJECTED),
          leaveRepository.count()
    );
  }
}