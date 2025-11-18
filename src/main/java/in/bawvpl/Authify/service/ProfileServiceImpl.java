package in.bawvpl.Authify.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import in.bawvpl.Authify.entity.UserEntity;
import in.bawvpl.Authify.io.ProfileRequest;
import in.bawvpl.Authify.io.ProfileResponse;
import in.bawvpl.Authify.repository.UserRepository;

/**
 * ProfileService implementation.
 *
 * This implementation expects UserEntity to have:
 *  - getUserId(), setVerifyOtp(String), setVerifyOtpExpireAt(Long)
 *  - boolean getter isAccountVerified()
 */
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

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
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public void sendVerificationOtp(String email) {
        setOtp(email);
    }

    /**
     * Convenience: return the application userId (UUID) for a given email.
     */
    @Override
    public String getLoggedInUserId(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntity::getUserId)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // --- helper methods ---

    private void setOtp(String email) {
        String otp = generateOtp();

        Optional<UserEntity> optUser = userRepository.findByEmail(email);
        if (optUser.isPresent()) {
            UserEntity user = optUser.get();

            user.setVerifyOtp(otp);

            // store expiry as epoch millis (use Long in entity)
            Long expireAtMillis = Instant.now().plusSeconds(5 * 60).toEpochMilli();
            user.setVerifyOtpExpireAt(expireAtMillis);

            userRepository.save(user);

            // ensure EmailService has this method
            emailService.sendVerificationOtpEmail(email, otp);
        } else {
            throw new RuntimeException("Cannot send OTP — user not found with email: " + email);
        }
    }

    private String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1_000_000));
    }

    private ProfileResponse convertToProfileResponse(UserEntity userEntity) {
        return ProfileResponse.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                // boolean getter should be isAccountVerified()
                .isAccountVerified(userEntity.isAccountVerified())
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
                .verifyOtpExpireAt(null)
                .resetOtp(null)
                .resetOtpExpireAt(null)
                .build();
    }
}
