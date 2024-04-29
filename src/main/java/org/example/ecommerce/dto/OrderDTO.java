package org.example.ecommerce.dto;

import org.example.ecommerce.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public record OrderDTO(
        Long id,
        String status,
        BigDecimal total,
        List<OrderItem> items,
        Long userId
) {
}
