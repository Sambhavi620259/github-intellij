package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.UserEntity;
import in.bawvpl.Authify.io.ProfileRequest;
import in.bawvpl.Authify.io.ProfileResponce;
import in.bawvpl.Authify.repostory.UserRepostory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepostory userRepostory;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // -------------------- CREATE PROFILE --------------------
    @Override
    public ProfileResponce createProfile(ProfileRequest request) {

        if (userRepostory.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        UserEntity userEntity = convertToUserEntity(request);
        userEntity = userRepostory.save(userEntity);

        return convertToProfileResponse(userEntity);
    }

    // -------------------- GET PROFILE --------------------
    @Override
    public ProfileResponce getProfile(String email) {
        UserEntity existingUser = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return convertToProfileResponse(existingUser);
    }

    // -------------------- SEND RESET OTP --------------------
    @Override
    public void sendResetOtp(String email) {

        UserEntity existingUser = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String otp = generateOtp();
        long expireTime = System.currentTimeMillis() + (15 * 60 * 1000); // 15 minutes

        existingUser.setResetOtp(otp);
        existingUser.setResetOtpExpireAt(expireTime);
        userRepostory.save(existingUser);

        try {
            emailService.sendResetOtpEmail(email, otp);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send reset OTP email");
        }
    }

    // -------------------- RESET PASSWORD USING OTP --------------------
    @Override
    public void resetPassword(String email, String otp, String newPassword) {

        UserEntity existingUser = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (!otp.equals(existingUser.getResetOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if (existingUser.getResetOtpExpireAt() < System.currentTimeMillis()) {
            throw new RuntimeException("OTP expired");
        }

        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setResetOtp(null);
        existingUser.setResetOtpExpireAt(0L);

        userRepostory.save(existingUser);
    }

    // -------------------- SEND VERIFICATION OTP --------------------
    @Override
    public void setOtp(String email) {

        UserEntity existingUser = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Already verified → no need to send OTP
        if (Boolean.TRUE.equals(existingUser.getIsAccountVerified())) {
            return;
        }

        String otp = generateOtp();
        long expireTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24 hours

        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expireTime);
        userRepostory.save(existingUser);

        try {
            emailService.sendVerificationOtpEmail(email, otp);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send verification email");
        }
    }

    // -------------------- VERIFY ACCOUNT USING OTP --------------------
    @Override
    public void verifyOtp(String email, String otp) {

        UserEntity existingUser = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (!otp.equals(existingUser.getVerifyOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if (existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()) {
            throw new RuntimeException("OTP expired");
        }

        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);

        userRepostory.save(existingUser);
    }

    // -------------------- GET LOGGED IN USER ID --------------------
    @Override
    public String getLoggedInUserId(String email) {

        UserEntity existingUser = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + email));

        return existingUser.getUserId();
    }

    // -------------------- FIXED: sendVerificationOtp FOR CONTROLLER --------------------
    @Override
    public void sendVerificationOtp(String email) {
        setOtp(email); // reuse the existing method
    }

    // -------------------- HELPERS --------------------
    private String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private ProfileResponce convertToProfileResponse(UserEntity userEntity) {
        return ProfileResponce.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .isAccountVerified(userEntity.getIsAccountVerified())
                .build();
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .resetOtpExpireAt(0L)
                .build();
    }
}
