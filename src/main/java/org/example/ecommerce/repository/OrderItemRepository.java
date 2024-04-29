package org.example.ecommerce.repository;


import org.example.ecommerce.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    OrderItem findByOrderIdAndProductId(Long orderId, Long productId);
}
