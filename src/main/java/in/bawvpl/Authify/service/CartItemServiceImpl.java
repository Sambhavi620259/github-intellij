package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.CartItem;
import in.bawvpl.Authify.io.CartItemRequest;
import in.bawvpl.Authify.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    @Override
    public CartItem addItem(String userId, CartItemRequest request) {
        CartItem item = CartItem.builder()
                .userId(userId)
                .productId(request.getProductId())
                .productName(request.getProductName())
                .price(request.getPrice() == null ? 0.0 : request.getPrice())
                .quantity(request.getQuantity() == null ? 1 : request.getQuantity())
                .build();
        return cartItemRepository.save(item);
    }

    @Override
    public List<CartItem> getItemsForUser(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void removeItem(String userId, String productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
