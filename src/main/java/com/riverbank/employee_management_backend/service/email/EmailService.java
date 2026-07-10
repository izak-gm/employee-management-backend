package com.riverbank.employee_management_backend.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${app.frontend-url}")
  private String frontendUrl;

  @Async
  public void sendInviteEmail(String toEmail, String firstName, String token) {
    Context context = new Context();
    context.setVariable("firstName", firstName);
    context.setVariable("setupLink", frontendUrl + "/setup-password?token=" + token);
    context.setVariable("expiryHours", 48);
    String html = templateEngine.process("emails/invite", context);
    send(toEmail, "You've been invited to Riverbank", html);
  }

  @Async
  public void sendPasswordResetEmail(String toEmail, String firstName, String token) {
    Context context = new Context();
    context.setVariable("firstName", firstName);
    context.setVariable("resetLink", frontendUrl + "/reset-password?token=" + token);
    context.setVariable("expiryHours", 1);
    String html = templateEngine.process("emails/reset-password", context);
    send(toEmail, "Reset your Riverbank password", html);
  }

  private void send(String to, String subject, String html) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom("noreply@riverbank.com");
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(html, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to send email to " + to, e);
    }
  }
}