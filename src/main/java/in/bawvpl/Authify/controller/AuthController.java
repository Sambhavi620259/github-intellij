package in.bawvpl.Authify.controller;

import in.bawvpl.Authify.io.AuthRequest;
import in.bawvpl.Authify.io.AuthResponse;
import in.bawvpl.Authify.io.RegisterRequest;
import in.bawvpl.Authify.service.AppUserDetailsService;
import in.bawvpl.Authify.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication controller: login and register endpoints.
 *
 * - Registration is delegated to AppUserDetailsService.registerUser(...)
 * - Login authenticates against the AuthenticationManager and returns a JWT on success.
 */
@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AppUserDetailsService appUserDetailsService;

    /**
     * Login endpoint.
     * Request body: { "email": "user@example.com", "password": "secret" }
     * Response: 200 OK { "email": "...", "token": "eyJ..." } on success
     *           401 Unauthorized { "error": "Invalid credentials", "message": "..." } on bad credentials
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
        } catch (AuthenticationException ex) {
            // Clear 401 JSON for Postman / debugging
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Invalid credentials",
                    "message", ex.getMessage() == null ? "Bad credentials" : ex.getMessage()
            ));
        }

        String token = jwtUtil.generateToken(req.getEmail());
        AuthResponse resp = new AuthResponse(req.getEmail(), token);
        return ResponseEntity.ok(resp);
    }

    /**
     * Registration endpoint.
     * Request body: RegisterRequest (name, email, password, phone)
     * Response: 201 Created { "message": "Registration successful" } or 409 if email exists
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            // delegate registration to service which will throw if email exists
            appUserDetailsService.registerUser(req);

            // optionally send verification email here using EmailService
            return ResponseEntity.status(201).body(Map.of("message", "Registration successful"));
        } catch (RuntimeException ex) {
            // AppUserDetailsService.registerUser throws RuntimeException("Email already exists")
            return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            // generic fallback
            return ResponseEntity.status(500).body(Map.of("error", "Registration failed", "message", ex.getMessage()));
        }
    }
}
