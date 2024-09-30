package com.example.ecommerce_backend.services.orders;

import com.example.ecommerce_backend.dtos.CartItemDTO;
import com.example.ecommerce_backend.dtos.OrderDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.*;
import com.example.ecommerce_backend.repositories.OrderDetailRepository;
import com.example.ecommerce_backend.repositories.OrderRepository;
import com.example.ecommerce_backend.repositories.ProductRepository;
import com.example.ecommerce_backend.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    private final ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        // Find the user by ID
        User user = userRepository
                .findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + orderDTO.getUserId()));

        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // Cập nhật các trường của đơn hàng từ orderDTO
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        // Convert OrderDTO to Order using ModelMapper
        order.setUser(user);
        order.setOrderDate(LocalDate.now()); // Set current date
        order.setStatus(OrderStatus.PENDING);

        // Validate shipping date
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Shipping date must be at least today!");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true); // Set as active
        order.setTotalMoney(orderDTO.getTotalMoney());

        orderRepository.save(order);
        // Initialize orderDetails list
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            // Create and populate OrderDetail from CartItemDTO
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();

            // Retrieve product from database
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productId));

            // Set details for OrderDetail
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setPrice(product.getPrice());
            orderDetail.setTotalMoney(product.getPrice() * quantity);

            // Handle attributes
            if (cartItemDTO.getAttributes() != null && !cartItemDTO.getAttributes().isEmpty()) {
                String attributesJson = objectMapper.writeValueAsString(cartItemDTO.getAttributes());
                orderDetail.setAttributes(attributesJson);
            } else {
                orderDetail.setAttributes("{}"); // Set empty JSON if no attributes
            }

            // Add to list
            orderDetails.add(orderDetail);
        }

        // Set orderDetails in the order object
        order.setOrderDetails(orderDetails);
        orderDetailRepository.saveAll(orderDetails);
        return order;
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) throws DataNotFoundException {
        return orderRepository.findOrdersByUserId(userId);
    }
}
