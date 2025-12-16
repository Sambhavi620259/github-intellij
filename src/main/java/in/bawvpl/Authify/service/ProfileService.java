package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.UserEntity;
import in.bawvpl.Authify.io.ProfileRequest;
import in.bawvpl.Authify.io.ProfileResponse;

public interface ProfileService {
    ProfileResponse createProfile(ProfileRequest request);
    ProfileResponse getProfile(String email);
    void sendResetOtp(String email);
    void resetPassword(String email, String otp, String newPassword);
    void sendVerificationOtp(String email);
    void verifyOtp(String email, String otp);
    void verifyKyc(String email);
    String getLoggedInUserId(String email);

    // convenience methods
    UserEntity save(UserEntity userEntity);
    boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
}
