package in.bawvpl.Authify.repository;

import in.bawvpl.Authify.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // get items by user
    List<CartItem> findByUserId(String userId);

    // find item by userId + productId
    Optional<CartItem> findByUserIdAndProductId(String userId, String productId);

    // delete by user + product
    void deleteByUserIdAndProductId(String userId, String productId);
}
