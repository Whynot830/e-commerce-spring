package org.example.ecommerce.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.mapper.OrderDTOMapper;
import org.example.ecommerce.model.Order;
import org.example.ecommerce.dto.OrderDTO;
import org.example.ecommerce.repository.OrderRepository;
import org.example.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.ecommerce.model.OrderStatus.CART;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final OrderDTOMapper mapper;

    public List<OrderDTO> readAll() {
        return orderRepo.findAll().stream().map(mapper).toList();
    }

    public OrderDTO read(Long id) {
        return orderRepo.findById(id)
                .map(mapper)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<OrderDTO> readByUserId(Long id) {
        userRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        var orders = orderRepo.findByUserId(id);
        return orders.stream().map(mapper).toList();
    }

    public void delete(Long id) {
        var order = orderRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        if (order.getStatus() == CART)
            throw new IllegalStateException("Cart cannot be deleted");
        orderRepo.delete(order);
    }
}
