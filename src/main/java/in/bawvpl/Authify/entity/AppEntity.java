package in.bawvpl.Authify.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "apps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique ID for app
    @Column(nullable = false, unique = true)
    private String appId;

    private String name;
    private String description;
    private String iconUrl;
    private Boolean active;
}
