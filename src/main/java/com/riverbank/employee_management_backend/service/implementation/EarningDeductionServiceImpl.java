package com.riverbank.employee_management_backend.service.implementation;

import com.riverbank.employee_management_backend.dto.payroll.DeductionTypeRequest;
import com.riverbank.employee_management_backend.dto.payroll.DeductionTypeResponse;
import com.riverbank.employee_management_backend.dto.payroll.EarningTypeRequest;
import com.riverbank.employee_management_backend.dto.payroll.EarningTypeResponse;
import com.riverbank.employee_management_backend.entity.payrolls.DeductionType;
import com.riverbank.employee_management_backend.entity.payrolls.EarningType;
import com.riverbank.employee_management_backend.enums.payrolls.DeductionCalculationType;
import com.riverbank.employee_management_backend.mapper.EarningDeductionMapper;
import com.riverbank.employee_management_backend.repository.payrolls.DeductionTypeRepository;
import com.riverbank.employee_management_backend.repository.payrolls.EarningTypeRepository;
import com.riverbank.employee_management_backend.service.EarningDeductionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EarningDeductionServiceImpl implements EarningDeductionService {

  private final EarningTypeRepository earningTypeRepo;
  private final DeductionTypeRepository deductionTypeRepo;
  private final EarningDeductionMapper mapper;

  // ══════════════════════════════════════════════════════════════════════════
  // EARNING TYPES
  // ══════════════════════════════════════════════════════════════════════════

  @Override
  @Transactional
  public EarningTypeResponse createEarningType(EarningTypeRequest request) {
    if (earningTypeRepo.existsByNameIgnoreCase(request.name())) {
      throw new RuntimeException("Earning type already exists: " + request.name());
    }

    EarningType earningType = EarningType.builder()
          .name(request.name().trim())
          .description(request.description())
          .taxable(request.taxable())
          .fixed(request.fixed())
          .active(true)
          .displayOrder(request.displayOrder() != null ? request.displayOrder() : 0)
          .build();

    log.info("Created earning type: {}", earningType.getName());
    return mapper.toResponse(earningTypeRepo.save(earningType));
  }

  @Override
  @Transactional
  public EarningTypeResponse updateEarningType(UUID id, EarningTypeRequest request) {
    EarningType earningType = findEarningTypeById(id);

    // Check name uniqueness — exclude self
    earningTypeRepo.findByNameIgnoreCase(request.name())
          .filter(e -> !e.getId().equals(id))
          .ifPresent(e -> {
            throw new RuntimeException(
                  "Earning type name already in use: " + request.name());
          });

    earningType.setName(request.name().trim());
    earningType.setDescription(request.description());
    earningType.setTaxable(request.taxable());
    earningType.setFixed(request.fixed());
    if (request.displayOrder() != null) {
      earningType.setDisplayOrder(request.displayOrder());
    }

    log.info("Updated earning type: {}", earningType.getName());
    return mapper.toResponse(earningTypeRepo.save(earningType));
  }

  @Override
  @Transactional(readOnly = true)
  public EarningTypeResponse getEarningTypeById(UUID id) {
    return mapper.toResponse(findEarningTypeById(id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<EarningTypeResponse> getAllEarningTypes() {
    return earningTypeRepo.findAllByOrderByDisplayOrderAsc()
          .stream().map(mapper::toResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<EarningTypeResponse> getActiveEarningTypes() {
    return earningTypeRepo.findByActiveTrueOrderByDisplayOrderAsc()
          .stream().map(mapper::toResponse).toList();
  }

  @Override
  @Transactional
  public void deactivateEarningType(UUID id) {
    EarningType earningType = findEarningTypeById(id);
    earningType.setActive(false);
    earningTypeRepo.save(earningType);
    log.info("Deactivated earning type: {}", earningType.getName());
  }

  @Override
  @Transactional
  public void activateEarningType(UUID id) {
    EarningType earningType = findEarningTypeById(id);
    earningType.setActive(true);
    earningTypeRepo.save(earningType);
    log.info("Activated earning type: {}", earningType.getName());
  }

  // ══════════════════════════════════════════════════════════════════════════
  // DEDUCTION TYPES
  // ══════════════════════════════════════════════════════════════════════════

  @Override
  @Transactional
  public DeductionTypeResponse createDeductionType(DeductionTypeRequest request) {
    if (deductionTypeRepo.existsByNameIgnoreCase(request.name())) {
      throw new RuntimeException("Deduction type already exists: " + request.name());
    }

    validateCalculationFields(request);

    DeductionType deductionType = DeductionType.builder()
          .name(request.name().trim())
          .description(request.description())
          .statutory(request.statutory())
          .taxable(request.taxable())
          .calculationType(request.calculationType())
          .fixedAmount(request.fixedAmount())
          .percentage(request.percentage())
          .active(true)
          .displayOrder(request.displayOrder() != null ? request.displayOrder() : 0)
          .build();

    log.info("Created deduction type: {}", deductionType.getName());
    return mapper.toResponse(deductionTypeRepo.save(deductionType));
  }

  @Override
  @Transactional
  public DeductionTypeResponse updateDeductionType(UUID id, DeductionTypeRequest request) {
    DeductionType deductionType = findDeductionTypeById(id);

    // Check name uniqueness — exclude self
    deductionTypeRepo.findByNameIgnoreCase(request.name())
          .filter(d -> !d.getId().equals(id))
          .ifPresent(d -> {
            throw new RuntimeException(
                  "Deduction type name already in use: " + request.name());
          });

    validateCalculationFields(request);

    deductionType.setName(request.name().trim());
    deductionType.setDescription(request.description());
    deductionType.setStatutory(request.statutory());
    deductionType.setTaxable(request.taxable());
    deductionType.setCalculationType(request.calculationType());
    deductionType.setFixedAmount(request.fixedAmount());
    deductionType.setPercentage(request.percentage());
    if (request.displayOrder() != null) {
      deductionType.setDisplayOrder(request.displayOrder());
    }

    log.info("Updated deduction type: {}", deductionType.getName());
    return mapper.toResponse(deductionTypeRepo.save(deductionType));
  }

  @Override
  @Transactional(readOnly = true)
  public DeductionTypeResponse getDeductionTypeById(UUID id) {
    return mapper.toResponse(findDeductionTypeById(id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<DeductionTypeResponse> getAllDeductionTypes() {
    return deductionTypeRepo.findAllByOrderByDisplayOrderAsc()
          .stream().map(mapper::toResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<DeductionTypeResponse> getActiveDeductionTypes() {
    return deductionTypeRepo.findByActiveTrueOrderByDisplayOrderAsc()
          .stream().map(mapper::toResponse).toList();
  }

  @Override
  @Transactional
  public void deactivateDeductionType(UUID id) {
    DeductionType deductionType = findDeductionTypeById(id);
    deductionType.setActive(false);
    deductionTypeRepo.save(deductionType);
    log.info("Deactivated deduction type: {}", deductionType.getName());
  }

  @Override
  @Transactional
  public void activateDeductionType(UUID id) {
    DeductionType deductionType = findDeductionTypeById(id);
    deductionType.setActive(true);
    deductionTypeRepo.save(deductionType);
    log.info("Activated deduction type: {}", deductionType.getName());
  }

  // ── Private helpers ────────────────────────────────────────────────────────

  private EarningType findEarningTypeById(UUID id) {
    return earningTypeRepo.findById(id)
          .orElseThrow(() -> new RuntimeException("Earning type not found: " + id));
  }

  private DeductionType findDeductionTypeById(UUID id) {
    return deductionTypeRepo.findById(id)
          .orElseThrow(() -> new RuntimeException("Deduction type not found: " + id));
  }

  /**
   * Validates that the correct amount field is provided
   * based on the selected calculation type.
   */
  private void validateCalculationFields(DeductionTypeRequest request) {
    if (request.calculationType() == DeductionCalculationType.FIXED) {
      if (request.fixedAmount() == null) {
        throw new RuntimeException("Fixed amount is required for FIXED calculation type");
      }
    } else if (request.calculationType() == DeductionCalculationType.PERCENTAGE) {
      if (request.percentage() == null) {
        throw new RuntimeException("Percentage is required for PERCENTAGE calculation type");
      }
    }
  }
}