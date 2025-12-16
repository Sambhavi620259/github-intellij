package in.bawvpl.Authify.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:no-reply@yourdomain.local}")
    private String from;

    public void sendVerificationOtpEmail(String to, String otp) {
        sendOtpEmail(to, "Your Authify verification code", otp);
    }

    public void sendResetOtpEmail(String to, String otp) {
        sendOtpEmail(to, "Authify password reset code", otp);
    }

    private void sendOtpEmail(String to, String subject, String otp) {
        log.info("Preparing OTP email to {}", to);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            String html = "<p>Your Authify code is: <b>" + otp + "</b></p>"
                    + "<p>If you did not request this, ignore this email.</p>";
            helper.setText(html, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(from);
            mailSender.send(message);
            log.info("Sent OTP email to {}", to);
        } catch (Exception ex) {
            log.error("Failed to send OTP email to {}: {}", to, ex.getMessage());
            throw new RuntimeException("Unable to send OTP email", ex);
        }
    }
}
