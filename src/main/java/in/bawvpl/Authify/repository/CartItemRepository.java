package in.bawvpl.Authify.repository;

import in.bawvpl.Authify.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(String userId);

    // Note: method name must match entity field names: productId (not itemId)
    void deleteByUserIdAndProductId(String userId, String productId);
}
