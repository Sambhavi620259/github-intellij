package in.bawvpl.Authify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String name;
    private String email;
    private String password;
    private String role;

    // New phone field
    private String phone;

    private boolean isAccountVerified;

    private Long verifyOtpExpireAt;
    private String verifyOtp;
    private String resetOtp;
    private Long resetOtpExpireAt;

    private Instant createdAt;
    private Instant updatedAt;

    // Custom getter for boolean (important!)
    public boolean isAccountVerified() {
        return isAccountVerified;
    }

    public void setAccountVerified(boolean accountVerified) {
        this.isAccountVerified = accountVerified;
    }

    public String getPhone() {
        return phone;
    }
}
