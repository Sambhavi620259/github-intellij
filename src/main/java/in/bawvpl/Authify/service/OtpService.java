package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.UserEntity;
import in.bawvpl.Authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final long OTP_EXPIRY_MS = 5 * 60 * 1000; // 5 minutes

    private final UserRepository userRepository;

    // ===============================
    // GENERATE OTP
    // ===============================
    public String generateLoginOtp(UserEntity user) {

        String otp = String.valueOf(
                100000 + new SecureRandom().nextInt(900000)
        );

        user.setVerifyOtp(otp);
        user.setVerifyOtpExpireAt(
                Instant.now().toEpochMilli() + OTP_EXPIRY_MS
        );

        userRepository.save(user);
        return otp;
    }

    // ===============================
    // VERIFY OTP WITH EXPIRY RESPONSE
    // ===============================
    public void verifyLoginOtp(UserEntity user, String otp) {

        if (user.getVerifyOtp() == null ||
                user.getVerifyOtpExpireAt() == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "OTP not generated. Please login again."
            );
        }

        // OTP expired
        if (Instant.now().toEpochMilli() > user.getVerifyOtpExpireAt()) {

            // clear expired OTP
            user.setVerifyOtp(null);
            user.setVerifyOtpExpireAt(null);
            userRepository.save(user);

            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    "OTP expired. Please request a new OTP."
            );
        }

        // OTP mismatch
        if (!user.getVerifyOtp().equals(otp)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid OTP"
            );
        }

        // OTP valid → clear OTP
        user.setVerifyOtp(null);
        user.setVerifyOtpExpireAt(null);
        user.setIsAccountVerified(true);

        userRepository.save(user);
    }
}
