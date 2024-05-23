package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
