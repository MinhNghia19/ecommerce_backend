package com.example.ecommerce_backend.services.orderdetails;


import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.OrderDetail;
import com.example.ecommerce_backend.repositories.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderDetailService implements IOrderDetailService{
//    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
//    private final ProductRepository productRepository;


//    @Override
//    public List<OrderDetail> getAllOrderDetailByUserId(Long userId) throws DataNotFoundException {
//        List<OrderDetail> orderDetails = orderDetailRepository.findAllByUserId(userId);
//        if (orderDetails.isEmpty()) {
//            throw new DataNotFoundException("No order details found for user with ID " + userId);
//        }
//        return orderDetails;
//    }

    @Override
    public List<OrderDetail> getOrderDetailByOrderId(Long orderId) throws DataNotFoundException {
        List<OrderDetail> orderDetails = orderDetailRepository.findOrdersDetailByOrderId(orderId);

        if (orderDetails.isEmpty()) {
            throw new DataNotFoundException("No order details found for user with ID " + orderId);
        }
        return orderDetails;
    }
}
