package in.bawvpl.Authify.controller;

import in.bawvpl.Authify.io.CartItemRequest;
import in.bawvpl.Authify.io.CartItemResponse;
import in.bawvpl.Authify.service.CartItemService;
import in.bawvpl.Authify.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0/cart")
public class CartItemController {

    private final CartItemService cartService;

    // Add item
    @PostMapping("/{userId}/add")
    public ResponseEntity<CartItemResponse> addItem(
            @PathVariable String userId,
            @Valid @RequestBody CartItemRequest req) {

        return ResponseEntity.ok(cartService.addItem(userId, req));
    }

    // Get items
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemResponse>> getItems(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getItemsForUser(userId));
    }

    // Remove item
    @DeleteMapping("/{userId}/{productId}/delete")
    public ResponseEntity<Void> removeItem(
            @PathVariable String userId,
            @PathVariable String productId) {

        cartService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }
}
