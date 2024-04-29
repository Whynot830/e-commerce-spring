package org.example.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.dto.OrderDTO;
import org.example.ecommerce.service.OrderService;
import org.example.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Orders API")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<List<OrderDTO>> readAll() {
        var orders = orderService.readAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> read(@PathVariable Long id) {
        var order = orderService.read(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping(params = "userId")
    public ResponseEntity<List<OrderDTO>> readByUserId(@RequestParam Long userId) {
        var orders = orderService.readByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping(params = "current-user")
    public ResponseEntity<List<OrderDTO>> readByAuthentication(
            Authentication authentication,
            @RequestParam(name = "current-user") String ignored
    ) {
        var userId = userService.getUserFromAuthentication(authentication).getId();
        return readByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
