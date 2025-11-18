package in.bawvpl.Authify.controller;

import in.bawvpl.Authify.entity.CartItem;
import in.bawvpl.Authify.io.CartItemRequest;
import in.bawvpl.Authify.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Validated
public class CartItemController {

    private final CartItemService cartService;

    // Add item
    @PostMapping("/{userId}/add")
    public ResponseEntity<CartItem> addItem(
            @PathVariable("userId") String userId,
            @RequestBody @Validated CartItemRequest req) {

        CartItem saved = cartService.addItem(userId, req);
        return ResponseEntity.ok(saved);
    }

    // Get items for a user
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getItems(@PathVariable("userId") String userId) {
        List<CartItem> items = cartService.getItemsForUser(userId);
        return ResponseEntity.ok(items);
    }

    // Remove item by productId
    @DeleteMapping("/{userId}/{productId}/delete")
    public ResponseEntity<Void> removeItem(
            @PathVariable("userId") String userId,
            @PathVariable("productId") String productId) {

        cartService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }
}
