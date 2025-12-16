package in.bawvpl.Authify.controller;

import in.bawvpl.Authify.io.AuthResponse;
import in.bawvpl.Authify.io.ProfileResponse;
import in.bawvpl.Authify.io.RegisterRequest;
import in.bawvpl.Authify.service.AppUserDetailsService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserDetailsService appUserDetailsService;

    // ===============================
    // REGISTER
    // ===============================
    @PostMapping("/register")
    public ResponseEntity<ProfileResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        ProfileResponse created =
                appUserDetailsService.registerUser(registerRequest);

        return ResponseEntity.ok(created);
    }

    // ===============================
    // LOGIN STEP 1 → EMAIL + PASSWORD
    // ===============================
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request) {

        boolean otpSent = appUserDetailsService.loginAndSendOtp(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message", "OTP sent successfully",
                        "otpSent", otpSent
                )
        );
    }

    // ===============================
    // LOGIN STEP 2 → VERIFY OTP + JWT
    // ===============================
    @PostMapping("/login/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        AuthResponse response =
                appUserDetailsService.verifyLoginOtp(
                        request.getEmail(),
                        request.getOtp()
                );

        return ResponseEntity.ok(response);
    }

    // ===============================
    // DTOs
    // ===============================
    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class VerifyOtpRequest {
        private String email;
        private String otp;
    }
}
