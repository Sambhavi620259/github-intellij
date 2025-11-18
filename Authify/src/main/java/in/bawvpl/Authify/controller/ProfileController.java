package in.bawvpl.Authify.controller;

import in.bawvpl.Authify.io.ProfileRequest;
import in.bawvpl.Authify.io.ProfileResponce;
import in.bawvpl.Authify.service.EmailService;
import in.bawvpl.Authify.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController

@RequiredArgsConstructor
public class ProfileController {
    private  final ProfileService profileService;
    private final EmailService emailService;
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponce register(@Valid @RequestBody ProfileRequest requst){
        //inside request - name, email , password ----->
        ProfileResponce response = profileService.createProfile(requst);
        //TODO: Send welcome email
        emailService.sendWelcomeEmail(response.getEmail(), response.getName());
        return response;
    }
    @GetMapping("/profile")
    public ProfileResponce getProfile(@CurrentSecurityContext(expression = "authentication?.name")String email){
        return profileService.getProfile(email);
    }
}
