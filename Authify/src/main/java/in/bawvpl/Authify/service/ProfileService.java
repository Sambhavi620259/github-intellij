package in.bawvpl.Authify.service;

import in.bawvpl.Authify.io.ProfileRequest;
import in.bawvpl.Authify.io.ProfileResponce;

public interface ProfileService{

    public ProfileResponce createProfile(ProfileRequest request);
    public ProfileResponce  getProfile(String email);

     void sendResetOtp(String email);
     void resetPassword(String email,String otp,String newPassword);
     void setOtp(String email);
     void verifyOtp(String email, String otp);
     String getLoggedInUserId(String email);

    void sendVerificationOtp(String email);
}
