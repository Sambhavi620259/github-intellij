package in.bawvpl.Authify.service;

import in.bawvpl.Authify.entity.CartItem;
import in.bawvpl.Authify.io.CartItemRequest;

import java.util.List;

public interface CartItemService {
    CartItem addItem(String userId, CartItemRequest request);
    List<CartItem> getItemsForUser(String userId);
    void removeItem(String userId, String productId);
}
