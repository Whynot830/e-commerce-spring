package org.example.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.ecommerce.model.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        Long id,
        String status,
        BigDecimal total,
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        List<OrderItem> items,
        Long userId
) {
}
