package com.riverbank.employee_management_backend.service.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${app.frontend-url}")
  private String frontendUrl;

  @Value("${app.mail.from}")
  private String from;

  @Value("${app.mail.from-name}")
  private String fromName;

  // ── Public methods ─────────────────────────────────────────────────────────

  @Async
  public void sendInviteEmail(String toEmail, String firstName, String token) {
    Context ctx = new Context();
    ctx.setVariable("firstName", firstName);
    ctx.setVariable("setupLink", frontendUrl + "/setup-password?token=" + token);
    ctx.setVariable("expiryHours", 48);

    send(toEmail,
          "You've been invited to Riverbank",
          templateEngine.process("emails/invite", ctx));
  }

  @Async
  public void sendPasswordResetEmail(String toEmail, String firstName, String token) {
    Context ctx = new Context();
    ctx.setVariable("firstName", firstName);
    ctx.setVariable("resetLink", frontendUrl + "/reset-password?token=" + token);
    ctx.setVariable("expiryHours", 1);

    send(toEmail,
          "Reset your Riverbank password",
          templateEngine.process("emails/reset-password", ctx));
  }

  @Async
  public void sendPayslip(String toEmail,
                          String employeeName,
                          String period,
                          String payrollNumber,
                          String employeeNumber,
                          BigDecimal grossPay,
                          BigDecimal totalDeductions,
                          BigDecimal paye,
                          BigDecimal incomeTax,
                          BigDecimal personalRelief,
                          BigDecimal nssf,
                          BigDecimal shif,
                          BigDecimal housingLevy,
                          BigDecimal netPay,
                          BigDecimal statutoryDeductions,
                          BigDecimal payAfterStatutoryDeductions,
                          byte[] pdfBytes) {

    Context ctx = new Context();
    ctx.setVariable("employeeName", employeeName);
    ctx.setVariable("period", period);
    ctx.setVariable("payrollNumber", payrollNumber);
    ctx.setVariable("employeeNumber", employeeNumber);
    ctx.setVariable("grossPay", formatAmount(grossPay));
    ctx.setVariable("totalDeductions", formatAmount(totalDeductions));
    ctx.setVariable("paye", formatAmount(paye));
    ctx.setVariable("incomeTax", formatAmount(incomeTax));
    ctx.setVariable("personalRelief", formatAmount(personalRelief));
    ctx.setVariable("nssf", formatAmount(nssf));
    ctx.setVariable("shif", formatAmount(shif));
    ctx.setVariable("housingLevy", formatAmount(housingLevy));
    ctx.setVariable("netPay", formatAmount(netPay));
    ctx.setVariable("statutoryDeductions", formatAmount(statutoryDeductions));
    ctx.setVariable("payAfterStatutoryDeductions", formatAmount(payAfterStatutoryDeductions));

    String attachmentName = "Payslip_" + period.replace(" ", "_") + ".pdf";

    send(toEmail,
          "Your Payslip for " + period,
          templateEngine.process("emails/payslip-email", ctx),
          attachmentName,
          pdfBytes);
  }

  // ── Private send helpers ───────────────────────────────────────────────────

  /**
   * Plain email — no attachment
   */
  private void send(String to, String subject, String html) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(from, fromName);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(html, true);

      mailSender.send(message);
      log.info("Email sent → [{}] to {}", subject, to);

    } catch (Exception e) {
      log.error("Failed to send email to {}: {}", to, e.getMessage());
      throw new RuntimeException("Failed to send email to " + to, e);
    }
  }

  /**
   * Email with a PDF attachment
   */
  private void send(String to, String subject, String html,
                    String attachmentName, byte[] attachmentBytes) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(from, fromName);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(html, true);
      helper.addAttachment(attachmentName,
            new ByteArrayResource(attachmentBytes),
            "application/pdf");

      mailSender.send(message);
      log.info("Email + attachment sent → [{}] to {}", subject, to);

    } catch (Exception e) {
      log.error("Failed to send email with attachment to {}: {}", to, e.getMessage());
      throw new RuntimeException("Failed to send email to " + to, e);
    }
  }

  // ── Util ───────────────────────────────────────────────────────────────────

  private String formatAmount(BigDecimal amount) {
    return NumberFormat.getNumberInstance(Locale.US).format(amount);
  }
}