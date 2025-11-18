package in.bawvpl.Authify.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // original file name
    private String filename;

    // content type e.g. image/png
    private String contentType;

    // use LONGBLOB for MySQL to be safe with large images
    @Lob
    @Column(name = "data", columnDefinition = "LONGBLOB")
    private byte[] data;
}
