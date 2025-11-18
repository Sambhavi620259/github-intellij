package in.bawvpl.Authify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    // -------------------- WELCOME EMAIL --------------------
    public void sendWelcomeEmail(String toEmail, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to Our Platform");
        message.setText(
                "Hello " + name + ",\n\n" +
                        "Thanks for registering with us!\n\n" +
                        "Regards,\nBold and Wise Ventures Pvt. Ltd."
        );
        mailSender.send(message);
    }

    // -------------------- RESET OTP EMAIL --------------------
    public void sendResetOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP");
        message.setText(
                "Your OTP for resetting your password is: " + otp +
                        "\nUse this OTP to proceed."
        );
        mailSender.send(message);
    }

    // -------------------- ACCOUNT VERIFICATION OTP EMAIL --------------------
    public void sendVerificationOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Account Verification OTP");
        message.setText(
                "Your verification OTP is: " + otp +
                        "\nUse this OTP to verify your account."
        );
        mailSender.send(message);
    }
}
