package in.bawvpl.Authify.service;

import in.bawvpl.Authify.io.CartItemRequest;
import in.bawvpl.Authify.io.CartItemResponse;

import java.util.List;

public interface CartService {

    List<CartItemResponse> getCart(String userId);

    CartItemResponse addItem(String userId, CartItemRequest request);

    CartItemResponse updateItem(String userId, Long itemId, int quantity);

    void removeItem(String userId, Long itemId);

    void clearCart(String userId);
}
