package in.bawvpl.Authify.io;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponce {
    private String userId;
    private String name;
    private String email;
    private Boolean isAccountVerified; // ✅ Make sure this is Boolean, not String
}

