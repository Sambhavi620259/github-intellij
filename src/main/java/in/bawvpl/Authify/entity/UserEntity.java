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
    @Column(name="id")
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name="name")
    private String name;

    @Column(name= "email", unique = true, nullable = false)
    private String email;

    // store phone in E.164 format (e.g. +911234567890)
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    // single role string (ROLE_USER / ROLE_ADMIN)
    @Column(name = "role")
    private String role;

    // Use wrapper Boolean so we can use null to mean "unknown"
    @Column(name = "is_account_verified")
    private Boolean isAccountVerified;

    @Column(name = "is_kyc_verified")
    private Boolean isKycVerified;

    @Column(name = "verifyOtp")
    private String verifyOtp;

    @Column(name = "verifyOtpExpireAt")
    private Long verifyOtpExpireAt;

    @Column(name = "resetOtp")
    private String resetOtp;

    @Column(name = "resetOtpExpireAt")
    private Long resetOtpExpireAt;

    @Column(name = "kycCompletedAt")
    private Long kycCompletedAt;

    // track when user created
    @Column(name = "created_at", updatable = false)
    private Long createdAt;

    // set createdAt automatically when persisting
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now().toEpochMilli();
        }
    }
}
