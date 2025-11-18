package in.bawvpl.Authify.controller;

import in.bawvpl.Authify.entity.UserEntity;
import in.bawvpl.Authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> profile(Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .map(u -> ResponseEntity.ok(
                        Map.of(
                                "id", u.getId(),
                                "userId", u.getUserId(),
                                "name", u.getName(),
                                "email", u.getEmail(),
                                "phone", u.getPhone(),
                                "role", u.getRole(),
                                "isAccountVerified", u.isAccountVerified(),
                                "createdAt", u.getCreatedAt()
                        )
                ))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "User not found")));
    }
}
