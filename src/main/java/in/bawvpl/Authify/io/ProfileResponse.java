package in.bawvpl.Authify.io; // use same package you expect; update imports accordingly

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {
    private String userId;
    private String name;
    private String email;
    private Boolean isAccountVerified;
}
