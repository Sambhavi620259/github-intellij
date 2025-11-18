package in.bawvpl.Authify.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple email service using Spring's JavaMailSender.
 *
 * Make sure you have the 'spring-boot-starter-mail' dependency in your pom.xml
 * and mail properties configured in application.properties/application.yml.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * From address (fallback provided).
     * Configure this in application.properties:
     * spring.mail.properties.mail.smtp.from=no-reply@yourdomain.com
     */
    @Value("${spring.mail.properties.mail.smtp.from:no-reply@example.com}")
    private String fromEmail;

    /**
     * Send a simple welcome email.
     */
    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to Our Platform");
            message.setText("Hello " + name + ",\n\n"
                    + "Thanks for registering with us!\n\n"
                    + "Regards,\nYour Company");
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send welcome email to {}: {}", toEmail, ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Send a password-reset OTP email.
     */
    public void sendResetOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset OTP");
            message.setText("Your OTP for resetting your password is: " + otp
                    + "\n\nThis OTP will expire in a few minutes. If you did not request a password reset, please ignore this message.");
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send reset OTP to {}: {}", toEmail, ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Send a verification OTP email (used when registering / verifying account).
     */
    public void sendVerificationOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Account Verification OTP");
            message.setText("Your OTP for account verification is: " + otp
                    + "\n\nThis OTP will expire in a few minutes. Do not share this OTP with anyone.");
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send verification OTP to {}: {}", toEmail, ex.getMessage(), ex);
            throw ex;
        }
    }
}
