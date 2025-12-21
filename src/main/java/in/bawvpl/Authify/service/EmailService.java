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

    @Value("${spring.mail.from:no-reply@authify.com}")
    private String from;

    // ------------------ SEND GENERAL OTP EMAIL ------------------
    private void sendOtpEmail(String to, String subject, String otp) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "utf-8");

            String html = """
                    <div>
                        <h2>Your Authify OTP</h2>
                        <p>Your one-time password is:</p>
                        <h1>%s</h1>
                        <p>This OTP is valid for 5–15 minutes depending on action.</p>
                        <br>
                        <p>If you did NOT request this, please ignore.</p>
                    </div>
                    """.formatted(otp);

            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(msg);
            log.info("OTP email sent to {}", to);

        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Unable to send OTP email");
        }
    }

    // ------------------ SEND REGISTRATION / VERIFY OTP ------------------
    public void sendVerificationOtpEmail(String to, String otp) {
        sendOtpEmail(to, "Your Authify Verification OTP", otp);
    }

    // ------------------ SEND RESET PASSWORD OTP ------------------
    public void sendResetOtpEmail(String to, String otp) {
        sendOtpEmail(to, "Authify Password Reset OTP", otp);
    }
}
