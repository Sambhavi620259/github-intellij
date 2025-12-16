package in.bawvpl.Authify.controller;

import in.bawvpl.Authify.io.ProfileResponse;
import in.bawvpl.Authify.entity.UserEntity;
import in.bawvpl.Authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ProfileResponse getProfile(Authentication authentication) {

        String email = authentication.getName(); // FROM JWT

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ProfileResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isAccountVerified(user.getIsAccountVerified())
                .isKycVerified(user.getIsKycVerified())
                .build();
    }
}
