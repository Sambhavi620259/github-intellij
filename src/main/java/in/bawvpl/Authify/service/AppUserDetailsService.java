package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.UserEntity;
import in.bawvpl.Authify.io.AuthResponse;
import in.bawvpl.Authify.io.ProfileResponse;
import in.bawvpl.Authify.io.RegisterRequest;
import in.bawvpl.Authify.repository.UserRepository;
import in.bawvpl.Authify.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    // ===============================
    // Spring Security Authentication
    // ===============================
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        // ✅ Always guarantee ROLE_USER
        String role = user.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_USER";
        }

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .build();
    }

    // ===============================
    // REGISTER
    // ===============================
    @Transactional
    public ProfileResponse registerUser(@Valid RegisterRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already registered");
        }

        UserEntity user = UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .name(req.getName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .password(passwordEncoder.encode(req.getPassword()))
                .role("ROLE_USER") // ✅ explicit
                .isAccountVerified(false)
                .isKycVerified(false)
                .build();

        userRepository.save(user);

        return mapToProfile(user);
    }

    // ===============================
    // LOGIN STEP 1 → PASSWORD + OTP
    // ===============================
    public boolean loginAndSendOtp(String email, String password) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String otp = otpService.generateLoginOtp(user);

        try {
            emailService.sendVerificationOtpEmail(user.getEmail(), otp);
            return true;
        } catch (Exception ex) {
            log.error("Failed to send OTP email", ex);
            return false;
        }
    }

    // ===============================
    // LOGIN STEP 2 → VERIFY OTP + JWT
    // ===============================
    @Transactional
    public AuthResponse verifyLoginOtp(String email, String otp) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "User not found"));

        otpService.verifyLoginOtp(user, otp);

        // ✅ Generate JWT after OTP success
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .profile(mapToProfile(user))
                .build();
    }

    // ===============================
    // Helper
    // ===============================
    private ProfileResponse mapToProfile(UserEntity user) {

        return ProfileResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isAccountVerified(Boolean.TRUE.equals(user.getIsAccountVerified()))
                .isKycVerified(Boolean.TRUE.equals(user.getIsKycVerified()))
                .build();
    }
}
