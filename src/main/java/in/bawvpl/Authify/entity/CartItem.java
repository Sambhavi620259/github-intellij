package in.bawvpl.Authify.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;        // <-- Matches your backend logic
    private String productId;
    private String productName;

    private double price;
    private int quantity;

    // IMPORTANT FIX
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
