package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.entity.payrolls.Payroll;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PayslipPdfService {

  byte[] generate(Payroll payroll); // existing single payslip

  byte[] generateBatchReport(List<Payroll> payrolls); // new
}