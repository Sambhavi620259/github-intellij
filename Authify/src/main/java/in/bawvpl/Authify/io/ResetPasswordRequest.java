package in.bawvpl.Authify.io;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "New Password is Required")
    private String newPassword;
    @NotBlank(message = "otp is Required")
    private String otp;
    @NotBlank (message = "Email is required")
    private String email;

}
