package in.bawvpl.Authify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * User identifier (UUID or DB id depending on your app)
     */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * Status string: "SUCCESS", "FAILED", etc.
     */
    @Column(name = "status")
    private String status;

    /**
     * Amount for the transaction
     */
    @Column(name = "amount")
    private Double amount;

    /**
     * Timestamp when the transaction occurred (UTC Instant)
     */
    @Column(name = "timestamp")
    private Instant timestamp;
}
