package com.example.ecommerce_backend.services.orders;



import com.example.ecommerce_backend.dtos.OrderDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.Order;
import com.example.ecommerce_backend.models.OrderDetail;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;
    List<Order> getOrdersByUserId(Long userId) throws DataNotFoundException;
}
