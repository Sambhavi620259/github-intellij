package in.bawvpl.Authify.service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import in.bawvpl.Authify.entity.UserEntity;
import in.bawvpl.Authify.io.ProfileRequest;
import in.bawvpl.Authify.io.ProfileResponse;
import in.bawvpl.Authify.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SmsService smsService; // optional; implement if you need SMS

    private static final long RESET_OTP_TTL_SECONDS = 15 * 60; // 15 minutes
    private static final long VERIFY_OTP_TTL_SECONDS = 24 * 60 * 60; // 24 hours

    // ---------------- CREATE PROFILE ----------------
    @Override
    @Transactional
    public ProfileResponse createProfile(ProfileRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is empty");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        UserEntity user = convertToUserEntity(request);
        user = userRepository.save(user);
        return convertToProfileResponse(user);
    }

    // ---------------- GET PROFILE ----------------
    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity user = findByEmailOrThrow(email);
        return convertToProfileResponse(user);
    }

    // ---------------- SEND RESET OTP (EMAIL) ----------------
    @Override
    @Transactional
    public void sendResetOtp(String email) {
        UserEntity user = findByEmailOrThrow(email);

        String otp = generateOtp();
        long expireAt = Instant.now().plusSeconds(RESET_OTP_TTL_SECONDS).toEpochMilli();

        user.setResetOtp(otp);
        user.setResetOtpExpireAt(expireAt);
        userRepository.save(user);

        // prefer email for OTP here
        emailService.sendResetOtpEmail(email, otp);
        // if you had smsService implement, you can call smsService.sendResetOtp(user.getPhoneNumber(), otp);
    }

    // ---------------- RESET PASSWORD USING OTP ----------------
    @Override
    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity user = findByEmailOrThrow(email);

        if (user.getResetOtp() == null || !user.getResetOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
        if (user.getResetOtpExpireAt() == null || Instant.now().toEpochMilli() > user.getResetOtpExpireAt()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setResetOtpExpireAt(null);
        userRepository.save(user);
    }

    // ---------------- SEND VERIFICATION OTP (EMAIL) ----------------
    @Override
    @Transactional
    public void sendVerificationOtp(String email) {
        UserEntity user = findByEmailOrThrow(email);

        if (Boolean.TRUE.equals(user.getIsKycVerified())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "KYC already verified");
        }
        if (Boolean.TRUE.equals(user.getIsAccountVerified())) {
            return; // already verified
        }

        String otp = generateOtp();
        long expireAt = Instant.now().plusSeconds(VERIFY_OTP_TTL_SECONDS).toEpochMilli();

        user.setVerifyOtp(otp);
        user.setVerifyOtpExpireAt(expireAt);
        userRepository.save(user);

        emailService.sendVerificationOtpEmail(email, otp);
    }

    // ---------------- VERIFY ACCOUNT USING OTP ----------------
    @Override
    @Transactional
    public void verifyOtp(String email, String otp) {
        UserEntity user = findByEmailOrThrow(email);

        if (user.getVerifyOtp() == null || !user.getVerifyOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
        if (user.getVerifyOtpExpireAt() == null || Instant.now().toEpochMilli() > user.getVerifyOtpExpireAt()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        user.setIsAccountVerified(true);
        user.setVerifyOtp(null);
        user.setVerifyOtpExpireAt(null);
        userRepository.save(user);
    }

    // ---------------- VERIFY KYC ----------------
    @Override
    @Transactional
    public void verifyKyc(String email) {
        UserEntity user = findByEmailOrThrow(email);
        user.setIsKycVerified(true);
        user.setKycCompletedAt(Instant.now().toEpochMilli());
        userRepository.save(user);
    }

    // ---------------- GET LOGGED IN USER ID ----------------
    @Override
    public String getLoggedInUserId(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntity::getUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // ---------------- Utility methods ----------------
    @Override
    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return findByEmailOrThrow(email);
    }

    private UserEntity findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email));
    }

    private String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100_000, 1_000_000));
    }

    private ProfileResponse convertToProfileResponse(UserEntity user) {
        return ProfileResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isAccountVerified(Boolean.TRUE.equals(user.getIsAccountVerified()))
                .isKycVerified(Boolean.TRUE.equals(user.getIsKycVerified()))
                .build();
    }

    private UserEntity convertToUserEntity(ProfileRequest req) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .name(req.getName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .password(passwordEncoder.encode(req.getPassword()))
                .isAccountVerified(false)
                .isKycVerified(false)
                .verifyOtp(null)
                .verifyOtpExpireAt(null)
                .resetOtp(null)
                .resetOtpExpireAt(null)
                .kycCompletedAt(null)
                .build();
    }
}
