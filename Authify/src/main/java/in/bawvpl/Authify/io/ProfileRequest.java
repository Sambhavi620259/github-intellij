package in.bawvpl.Authify.io;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ProfileRequest {
    @NotBlank(message = "Name should be not empty")
    private String name;
    @Email(message = "Enter valid Email address")
    @NotNull(message = "Email should be not empty ")
    private String email;
    @Size(min = 8, message = "Password must be atleast 8 character")
    private String password;
}
