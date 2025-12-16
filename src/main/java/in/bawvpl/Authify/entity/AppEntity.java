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
    @Column(name = "id")
    private Long id;

    // Unique ID for app
    @Column(nullable = false, unique = true)
    private String appId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "iconUrl")
    private String iconUrl;

    @Column(name = "active")
    private Boolean active;
}
