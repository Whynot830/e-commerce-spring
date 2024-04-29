package org.example.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.exception.NoAuthenticationException;
import org.example.ecommerce.model.Order;
import org.example.ecommerce.model.User;
import org.example.ecommerce.service.CartService;
import org.example.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Cart API")
public class CartController {
    private final UserService userService;
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Order> read(Authentication authentication) {
        var user = userService.getUserFromAuthentication(authentication);
        var cart = cartService.read(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(Authentication authentication) {
        var user = userService.getUserFromAuthentication(authentication);
        var cart = cartService.checkout(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping(value = "/items", params = "productId")
    public ResponseEntity<Order> addItem(
            Authentication authentication,
            @RequestParam Long productId
    ) {
        var user = userService.getUserFromAuthentication(authentication);
        var cart = cartService.addItem(productId, user);
        return ResponseEntity.ok(cart);
    }

    @PatchMapping(value = "/items/{itemId}", params = "quantity")
    public ResponseEntity<Order> updateQuantity(
            Authentication authentication,
            @PathVariable Long itemId,
            @RequestParam Integer quantity
    ) {
        var user = userService.getUserFromAuthentication(authentication);
        var cart = cartService.updateItemQuantity(itemId, quantity, user);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping(value = "/items/{itemId}")
    public ResponseEntity<Order> deleteItem(
            Authentication authentication,
            @PathVariable Long itemId
    ) {
        var user = userService.getUserFromAuthentication(authentication);
        var cart = cartService.deleteItem(itemId, user);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items")
    public ResponseEntity<Order> clear(Authentication authentication) {
        var user = userService.getUserFromAuthentication(authentication);
        var cart = cartService.clear(user);
        return ResponseEntity.ok(cart);
    }
}
