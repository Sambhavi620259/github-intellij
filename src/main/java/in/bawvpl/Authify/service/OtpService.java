package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.UserEntity;

public interface OtpService {

    String generateLoginOtp(UserEntity user);

    void verifyLoginOtp(UserEntity user, String otp);

    String generateOtp(); // general purpose OTP if needed
}
