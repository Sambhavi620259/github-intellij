package in.bawvpl.Authify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "kyc_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User identifier (UUID or DB id depending on your app)
     */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * Type of document: "PAN", "AADHAAR", "PASSPORT", etc.
     */
    @Column(name = "document_type")
    private String documentType;

    /**
     * URL or storage key for the uploaded document
     */
    @Column(name = "document_url", length = 2000)
    private String documentUrl;

    /**
     * Whether KYC was marked completed for this doc
     */
    @Column(name = "completed")
    private Boolean completed;

    /**
     * When this KYC doc was uploaded
     */
    @Column(name = "uploaded_at")
    private Instant uploadedAt;
}
