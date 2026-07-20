package com.riverbank.employee_management_backend.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.riverbank.employee_management_backend.entity.Employee;
import com.riverbank.employee_management_backend.entity.payrolls.Payroll;
import com.riverbank.employee_management_backend.entity.payrolls.PayrollDeduction;
import com.riverbank.employee_management_backend.entity.payrolls.PayrollEarning;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class PayslipPdfService {

  // ── Brand colours ──────────────────────────────────────────────────────────
  private static final DeviceRgb BLUE = new DeviceRgb(21, 101, 192);
  private static final DeviceRgb PURPLE = new DeviceRgb(106, 27, 154);
  private static final DeviceRgb BLUE_LIGHT = new DeviceRgb(232, 240, 254);
  private static final DeviceRgb GREY_BG = new DeviceRgb(245, 245, 245);
  private static final DeviceRgb GREY_TEXT = new DeviceRgb(117, 117, 117);

  // ── Fonts ──────────────────────────────────────────────────────────────────
  private PdfFont regular;
  private PdfFont bold;

  private void initFonts() throws IOException {
    regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
  }

  // ── Entry point ────────────────────────────────────────────────────────────

  public byte[] generate(Payroll payroll) {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      initFonts();

      PdfWriter writer = new PdfWriter(out);
      PdfDocument pdf = new PdfDocument(writer);
      Document document = new Document(pdf, PageSize.A4);
      document.setMargins(36, 40, 36, 40);
      document.setFont(regular);

      Employee emp = payroll.getEmployee();
      String period = Month.of(payroll.getPayrollMonth())
            .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            + " " + payroll.getPayrollYear();

      addHeader(document, period);
      addEmployeeInfo(document, emp, payroll, period);
      addEarnings(document, payroll.getEarnings(), payroll.getGrossPay());
      addDeductions(document, payroll.getDeductions(), payroll.getTotalDeductions());
      addNetPay(document, payroll.getNetPay());
      addEmployerContributions(document, payroll.getEmployerNssf(), payroll.getEmployerShif());
      addFooter(document);

      document.close();
      return out.toByteArray();

    } catch (Exception e) {
      log.error("Failed to generate payslip PDF for payroll {}: {}",
            payroll.getPayrollNumber(), e.getMessage());
      throw new RuntimeException("Payslip PDF generation failed", e);
    }
  }

  // ── Sections ───────────────────────────────────────────────────────────────

  private void addHeader(Document doc, String period) {
    doc.add(new Paragraph("RIVERBANK")
          .setFont(bold)
          .setFontSize(22)
          .setFontColor(BLUE)
          .setTextAlignment(TextAlignment.CENTER));

    doc.add(new Paragraph("PAYSLIP — " + period.toUpperCase())
          .setFont(regular)
          .setFontSize(11)
          .setFontColor(GREY_TEXT)
          .setTextAlignment(TextAlignment.CENTER)
          .setMarginBottom(4));

    doc.add(new Paragraph("")
          .setBorderBottom(new SolidBorder(BLUE, 2))
          .setMarginBottom(12));
  }

  private void addEmployeeInfo(Document doc, Employee emp, Payroll payroll, String period) {
    sectionTitle(doc, "EMPLOYEE INFORMATION");

    Table t = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
          .useAllAvailableWidth()
          .setMarginBottom(16);

    addInfoCell(t, "Employee Name", emp.getFirstName() + " " + emp.getLastName());
    addInfoCell(t, "Employee No.", emp.getEmployeeNumber());
    addInfoCell(t, "Payroll No.", payroll.getPayrollNumber());
    addInfoCell(t, "Pay Period", period);
    addInfoCell(t, "Department", emp.getDepartment() != null ? emp.getDepartment().getName() : "—");
    addInfoCell(t, "Position", emp.getPosition() != null ? emp.getPosition().getName() : "—");
    addInfoCell(t, "Payment Date", payroll.getPaymentDate() != null
          ? payroll.getPaymentDate().toString() : "—");
    addInfoCell(t, "Payment Ref.", payroll.getPaymentReference() != null
          ? payroll.getPaymentReference() : "—");
    doc.add(t);
  }

  private void addEarnings(Document doc, List<PayrollEarning> earnings, BigDecimal grossPay) {
    sectionTitle(doc, "EARNINGS");

    Table t = lineItemTable();
    tableHeader(t, "Description", "Taxable", "Amount (KES)");

    for (PayrollEarning e : earnings) {
      if (e.getAmount().compareTo(BigDecimal.ZERO) == 0) continue;
      bodyRow(t,
            e.getEarningType().getName(),
            e.getEarningType().isTaxable() ? "Yes" : "No",
            fmt(e.getAmount()));
    }

    totalRow(t, "GROSS PAY", fmt(grossPay));
    doc.add(t.setMarginBottom(16));
  }

  private void addDeductions(Document doc, List<PayrollDeduction> deductions,
                             BigDecimal totalDeductions) {
    sectionTitle(doc, "DEDUCTIONS");

    Table t = lineItemTable();
    tableHeader(t, "Description", "Statutory", "Amount (KES)");

    for (PayrollDeduction d : deductions) {
      if (d.getAmount().compareTo(BigDecimal.ZERO) == 0) continue;
      bodyRow(t,
            d.getDeductionType().getName(),
            d.getDeductionType().isStatutory() ? "Yes" : "No",
            fmt(d.getAmount()));
    }

    totalRow(t, "TOTAL DEDUCTIONS", fmt(totalDeductions));
    doc.add(t.setMarginBottom(16));
  }

  private void addNetPay(Document doc, BigDecimal netPay) {
    Table t = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
          .useAllAvailableWidth()
          .setMarginBottom(16);

    t.addCell(new Cell()
          .add(new Paragraph("NET PAY")
                .setFont(bold)
                .setFontSize(13)
                .setFontColor(ColorConstants.WHITE))
          .setBackgroundColor(BLUE)
          .setPadding(10)
          .setBorder(Border.NO_BORDER));

    t.addCell(new Cell()
          .add(new Paragraph("KES " + fmt(netPay))
                .setFont(bold)
                .setFontSize(13)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.RIGHT))
          .setBackgroundColor(BLUE)
          .setPadding(10)
          .setBorder(Border.NO_BORDER));

    doc.add(t);
  }

  private void addEmployerContributions(Document doc,
                                        BigDecimal employerNssf,
                                        BigDecimal employerShif) {
    sectionTitle(doc, "EMPLOYER CONTRIBUTIONS (For Reference Only)");

    Table t = lineItemTable();
    tableHeader(t, "Description", "", "Amount (KES)");
    bodyRow(t, "Employer NSSF", "", fmt(employerNssf));
    bodyRow(t, "Employer SHIF", "", fmt(employerShif));
    totalRow(t, "TOTAL", fmt(employerNssf.add(employerShif)));

    doc.add(t.setMarginBottom(16));
  }

  private void addFooter(Document doc) {
    doc.add(new Paragraph("")
          .setBorderTop(new SolidBorder(GREY_TEXT, 0.5f))
          .setMarginTop(8));

    doc.add(new Paragraph(
          "This is a system-generated payslip and does not require a signature.\n" +
                "For queries contact HR at hr@riverbank.com")
          .setFont(regular)
          .setFontSize(8)
          .setFontColor(GREY_TEXT)
          .setTextAlignment(TextAlignment.CENTER)
          .setMarginTop(6));
  }

  // ── Table builders ─────────────────────────────────────────────────────────

  private Table lineItemTable() {
    return new Table(UnitValue.createPercentArray(new float[]{5, 2, 2}))
          .useAllAvailableWidth();
  }

  private void tableHeader(Table t, String col1, String col2, String col3) {
    for (String label : new String[]{col1, col2, col3}) {
      t.addHeaderCell(new Cell()
            .add(new Paragraph(label)
                  .setFont(bold)
                  .setFontSize(9)
                  .setFontColor(ColorConstants.WHITE))
            .setBackgroundColor(PURPLE)
            .setPadding(6)
            .setBorder(Border.NO_BORDER));
    }
  }

  private void bodyRow(Table t, String label, String mid, String amount) {
    t.addCell(bodyCell(label).setBackgroundColor(GREY_BG));
    t.addCell(bodyCell(mid));
    t.addCell(bodyCell(amount).setTextAlignment(TextAlignment.RIGHT));
  }

  private void totalRow(Table t, String label, String amount) {
    t.addCell(new Cell(1, 2)
          .add(new Paragraph(label)
                .setFont(bold)
                .setFontSize(9))
          .setPadding(6)
          .setBorder(Border.NO_BORDER));

    t.addCell(new Cell()
          .add(new Paragraph(amount)
                .setFont(bold)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.RIGHT))
          .setBackgroundColor(BLUE_LIGHT)
          .setPadding(6)
          .setBorder(Border.NO_BORDER));
  }

  private Cell bodyCell(String text) {
    return new Cell()
          .add(new Paragraph(text)
                .setFont(regular)
                .setFontSize(9))
          .setPadding(5)
          .setBorder(Border.NO_BORDER)
          .setBorderBottom(new SolidBorder(GREY_BG, 0.5f));
  }

  // ── Info grid cell ─────────────────────────────────────────────────────────

  private void addInfoCell(Table t, String label, String value) {
    t.addCell(new Cell()
          .add(new Paragraph(label)
                .setFont(regular)
                .setFontSize(8)
                .setFontColor(GREY_TEXT)
                .setMarginBottom(2))
          .add(new Paragraph(value != null ? value : "—")
                .setFont(bold)
                .setFontSize(9))
          .setPadding(6)
          .setBackgroundColor(GREY_BG)
          .setBorder(Border.NO_BORDER)
          .setBorderBottom(new SolidBorder(ColorConstants.WHITE, 2)));
  }

  // ── Section title ──────────────────────────────────────────────────────────

  private void sectionTitle(Document doc, String title) {
    doc.add(new Paragraph(title)
          .setFont(bold)
          .setFontSize(9)
          .setFontColor(ColorConstants.WHITE)
          .setBackgroundColor(BLUE)
          .setPadding(5)
          .setMarginBottom(0)
          .setMarginTop(4));
  }

  // ── Number format ──────────────────────────────────────────────────────────

  private String fmt(BigDecimal amount) {
    return NumberFormat.getNumberInstance(Locale.US).format(amount);
  }
}