package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.UserEntity;
import in.bawvpl.Authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final UserRepository userRepository;

    private static final long LOGIN_OTP_EXPIRY_SECONDS = 5 * 60; // 5 minutes

    // -----------------------
    // GENERAL OTP GENERATOR
    // -----------------------
    @Override
    public String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }

    // -----------------------
    // LOGIN OTP GENERATION
    // -----------------------
    @Override
    @Transactional
    public String generateLoginOtp(UserEntity user) {
        String otp = generateOtp();
        long expiry = Instant.now().plusSeconds(LOGIN_OTP_EXPIRY_SECONDS).toEpochMilli();

        user.setVerifyOtp(otp);
        user.setVerifyOtpExpireAt(expiry);

        userRepository.save(user);
        log.info("Login OTP generated for {} => {}", user.getEmail(), otp);

        return otp;
    }

    // -----------------------
    // VERIFY LOGIN OTP
    // -----------------------
    @Override
    @Transactional
    public void verifyLoginOtp(UserEntity user, String otp) {

        if (user.getVerifyOtp() == null || !user.getVerifyOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }

        if (user.getVerifyOtpExpireAt() == null ||
                Instant.now().toEpochMilli() > user.getVerifyOtpExpireAt()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        // OTP is valid → clear it
        user.setVerifyOtp(null);
        user.setVerifyOtpExpireAt(null);
        userRepository.save(user);

        log.info("Login OTP verified successfully for {}", user.getEmail());
    }
}

