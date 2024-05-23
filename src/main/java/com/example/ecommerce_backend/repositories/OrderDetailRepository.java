package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
}
