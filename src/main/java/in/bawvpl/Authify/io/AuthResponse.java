package in.bawvpl.Authify.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple DTO returned after login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String email;
    private String token;
}
