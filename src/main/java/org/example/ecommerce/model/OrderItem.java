package org.example.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Iterator;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue
    private Long id;

    private Integer quantity;

    @ManyToOne
    private Product product;

    private BigDecimal total;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    private Order order;

    @PostUpdate
    @PostPersist
    private void postPersistOrUpdate() {
        recalculateTotal();
    }

    public void recalculateTotal() {
        total = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        if (order != null)
            order.recalculateTotal();
    }
}
