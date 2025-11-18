package in.bawvpl.Authify.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User UUID
    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String productId;

    private String productName;
    private double price;
    private int quantity;
}
