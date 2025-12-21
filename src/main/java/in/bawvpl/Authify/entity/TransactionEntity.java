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
    private Long id;

    private String userId;

    // CREDIT or DEBIT
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Instant createdAt;
}
