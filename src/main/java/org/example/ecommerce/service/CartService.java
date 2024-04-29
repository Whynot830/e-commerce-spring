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
import java.math.BigDecimal;
import java.util.Collections;

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
                .append("<h2>Congratulations, you've confirmed your order!</h2>")
                .append("<table style=\"sp\">")
                .append("<thead><tr>")
                .append("<th colspan=\"4\"><h3>Ordered items</h3></th>")
                .append("</tr></thead>");

        for (var item : items) {
            var product = item.getProduct();
            sb
                    .append("<tr>")
                    .append("<td style=\"padding: 0; width: 50%\"><h4 style=\"margin: 8px 0\">")
                    .append(product.getTitle())
                    .append("</h4></td>")
                    .append("<td style=\"padding: 0; text-align: center\"><h4 style=\"margin: 8px 0\">")
                    .append(product.getCategory().getName().toUpperCase())
                    .append("</h4></td>")
                    .append("<td style=\"padding: 0; text-align: center\"><h4 style=\"margin: 8px 0\">")
                    .append(item.getQuantity())
                    .append(" pcs.</h4></td>")
                    .append("<td style=\"padding: 0; text-align: center\"><h4 style=\"margin: 8px 0\">USD $ ")
                    .append(product.getPrice())
                    .append("</h4></td>")
                    .append("</tr>");
        }
        sb.append("</tbody></table>")
                .append("<h3>Total: USD $ ")
                .append(cart.getTotal())
                .append("</h3>")
                .append("<h4>Best regards,<br/>WHYNOTPC</h4></div>");

        try {
            emailService.sendMail(user.getEmail(), sb.toString());
        } catch (MessagingException ex) {
            System.out.println(ex.getMessage());
        }

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
