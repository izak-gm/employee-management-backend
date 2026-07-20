package com.riverbank.employee_management_backend.repository.payrolls;

import com.riverbank.employee_management_backend.entity.payrolls.DeductionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeductionTypeRepository extends JpaRepository<DeductionType, UUID> {

  Optional<DeductionType> findByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCase(String name);

  // Only active types — used in payroll generation
  List<DeductionType> findByActiveTrueOrderByDisplayOrderAsc();

  // All types including inactive — used in admin management
  List<DeductionType> findAllByOrderByDisplayOrderAsc();

  // Separate statutory from non-statutory for reports
  List<DeductionType> findByStatutoryTrueAndActiveTrueOrderByDisplayOrderAsc();
}
