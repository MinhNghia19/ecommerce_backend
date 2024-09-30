package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.models.Order;
import com.example.ecommerce_backend.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
    List<OrderDetail> findOrdersDetailByOrderId(Long orderId);
}
