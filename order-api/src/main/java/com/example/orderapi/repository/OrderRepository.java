package com.example.orderapi.repository;

import com.example.orderapi.entity.OrderEntity;
import com.example.orderapi.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByCustomerEmail(String customerEmail);

    List<OrderEntity> findByStatus(OrderStatus status);

    List<OrderEntity> findByCustomerNameContainingIgnoreCase(String customerName);
}
