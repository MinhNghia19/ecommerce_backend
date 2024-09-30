package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.models.Order;
import com.example.ecommerce_backend.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

//    @Query("SELECT o FROM Order o WHERE o.userId = :userId")
//    List<Order> findOrdersByUserId(@Param("userId") Long userId);
      List<Order> findOrdersByUserId(Long userId);

}
