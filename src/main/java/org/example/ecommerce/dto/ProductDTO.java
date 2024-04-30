package org.example.ecommerce.dto;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,
        String title,
        String description,
        BigDecimal price,
        String imgName,
        String category
) {
}
