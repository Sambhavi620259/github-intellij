package in.bawvpl.Authify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String phoneNumber;

    private String password;

    private String role;

    private Boolean isAccountVerified;

    private Boolean isKycVerified;

    // Login OTP
    private String verifyOtp;
    private Long verifyOtpExpireAt;

    // Register OTP
    private String registerOtp;
    private Long registerOtpExpireAt;

    // Reset Password OTP
    private String resetOtp;
    private Long resetOtpExpireAt;

    @Column(updatable = false)
    private Long createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now().toEpochMilli();
    }
}
