package org.example.ecommerce.mapper;

import org.example.ecommerce.dto.OrderDTO;
import org.example.ecommerce.model.Order;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class OrderDTOMapper implements Function<Order, OrderDTO> {
    @Override
    public OrderDTO apply(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getStatus().name(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getItems(),
                order.getUser().getId());
    }
}
