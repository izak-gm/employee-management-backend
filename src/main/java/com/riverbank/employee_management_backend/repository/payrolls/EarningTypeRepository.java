package com.riverbank.employee_management_backend.repository.payrolls;

import com.riverbank.employee_management_backend.entity.payrolls.EarningType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EarningTypeRepository extends JpaRepository<EarningType, UUID> {

  Optional<EarningType> findByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCase(String name);

  // Only active types — used in payroll generation dropdowns
  List<EarningType> findByActiveTrueOrderByDisplayOrderAsc();

  // All types including inactive — used in admin management
  List<EarningType> findAllByOrderByDisplayOrderAsc();
}
