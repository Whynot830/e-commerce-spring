package org.example.ecommerce.service;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.model.Order;
import org.example.ecommerce.model.OrderItem;
import org.example.ecommerce.model.User;
import org.example.ecommerce.repository.OrderItemRepository;
import org.example.ecommerce.repository.OrderRepository;
import org.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

import static org.example.ecommerce.model.OrderStatus.CART;
import static org.example.ecommerce.model.OrderStatus.COMPLETED;

@Service
@RequiredArgsConstructor
public class CartService {
    private final OrderRepository orderRepo;
    private final EmailService emailService;
    private final ProductRepository productRepo;
    private final OrderItemRepository orderItemRepo;

    private Order getCart(User user) {
        return orderRepo.findCartByUser(user).orElseThrow(IllegalStateException::new);
    }

    public Order read(User user) {
        return getCart(user);
    }

    public Order checkout(User user) {
        var cart = getCart(user);

        var items = cart.getItems();
        if (items.isEmpty())
            throw new IllegalStateException("Checking out with an empty cart is not allowed");

        StringBuilder sb = new StringBuilder();
        sb
                .append("<div style=\"display: flex; align-content: center; flex-direction: column; gap: 0.5rem; width: 100%\">")
//                .append("<h2>Congratulations, you've confirmed your order!</h2>")
                .append("<h2>Вы успешно оформили заказ!</h2>")
                .append("<table>")
                .append("<thead><tr>")
//                .append("<th colspan=\"4\"><h3>Ordered items</h3></th>")
                .append("<th colspan=\"4\"><h3>Выбранные продукты</h3></th>")
                .append("</tr>")
                .append("<tr>")
//                .append("<th style=\"text-align: start\"><h4 style=\"margin: 8px 0\">Product Name</h4></th>")
                .append("<th style=\"text-align: start\"><h4 style=\"margin: 8px 0\">Название</h4></th>")
//                .append("<th><h4 style=\"margin: 8px 0\">Category</h4></th>")
                .append("<th><h4 style=\"margin: 8px 0\">Категория</h4></th>")
//                .append("<th><h4 style=\"margin: 8px 0\">Quantity</h4></th>")
//                .append("<th><h4 style=\"margin: 8px 0\">Price</h4></th>")
                .append("<th><h4 style=\"margin: 8px 0\">Цена</h4></th>")
                .append("</tr></thead>");


        for (var item : items) {
            var product = item.getProduct();
            sb
                    .append("<tr>")
                    .append("<td style=\"padding: 0; width: 25%\"><h4 style=\"margin: 8px 0\">")
                    .append(product.getTitle())
                    .append("</h4></td>")
                    .append("<td style=\"padding: 0; text-align: center\"><h4 style=\"margin: 8px 0\">")
                    .append(product.getCategory().getName().toUpperCase())
                    .append("</h4></td>")
//                    .append("<td style=\"padding: 0; text-align: center\"><h4 style=\"margin: 8px 0\">")
//                    .append(item.getQuantity())
//                    .append(" pcs.</h4></td>")
                    .append("<td style=\"padding: 0; text-align: center\"><h4 style=\"margin: 8px 0\">$")
                    .append(product.getPrice())
                    .append("</h4></td>")
                    .append("</tr>");
        }
        sb.append("</tbody></table>")
//                .append("<h3>Total: USD $ ")
                .append("<h3>Итого: USD $ ")
                .append(cart.getTotal())
                .append("</h3>")
//                .append("<h4>Best regards,<br/>Furniture Shop</h4></div>");
                .append("<h4>С наилучшими пожеланиями,<br/>Furniture Shop</h4></div>");
//        try {
//            Path outputFile = Paths.get("order-preview.html");
//            Files.write(outputFile, sb.toString().getBytes());
//            System.out.println("HTML preview saved to: " + outputFile.toAbsolutePath());
//        } catch (IOException e) {
//            System.err.println("Error writing HTML preview to file: " + e.getMessage());
//        }
        try {
            emailService.sendMail("Ваш заказ в Furniture Shop (no reply)", user.getEmail(), sb.toString());
//            emailService.sendMail("Your order in Furniture Shop (no reply)", user.getEmail(), sb.toString());
        } catch (Exception ex) {
            throw new RuntimeException("Something went wrong when sending email: " + ex.getMessage());
        }

        cart.setCreatedAt(LocalDateTime.now());
        cart.setStatus(COMPLETED);

        orderRepo.save(Order.builder()
                .status(CART)
                .total(BigDecimal.valueOf(0))
                .items(Collections.emptyList())
                .user(user)
                .build());

        return cart;
    }

    public Order addItem(Long productId, User user) {
        var cart = getCart(user);
        var product = productRepo.findById(productId).orElseThrow(EntityNotFoundException::new);
        var item = orderItemRepo.findByOrderIdAndProductId(cart.getId(), productId);

        if (item != null) item.setQuantity(item.getQuantity() + 1);
        else item = OrderItem.builder().quantity(1).product(product).order(cart).build();

        orderItemRepo.save(item);
        orderRepo.save(cart);

        return cart;
    }

    public Order updateItemQuantity(Long itemId, Integer quantity, User user) {
        var cart = getCart(user);
        var item = orderItemRepo.findById(itemId).orElseThrow(EntityNotFoundException::new);

        if (quantity < 1)
            throw new IllegalArgumentException("Quantity must be greater than zero");

        item.setQuantity(quantity);
        orderItemRepo.save(item);
        orderRepo.save(cart);

        return cart;
    }

    public Order deleteItem(Long itemId, User user) {
        var cart = getCart(user);
        var item = orderItemRepo.findById(itemId).orElseThrow(EntityNotFoundException::new);
        orderItemRepo.delete(item);
        cart.recalculateTotal();
        orderRepo.save(cart);
        return cart;
    }

    public Order clear(User user) {
        var cart = getCart(user);
        var items = cart.getItems();
        cart.setItems(Collections.emptyList());
        orderItemRepo.deleteAll(items);

        return cart;
    }
}
