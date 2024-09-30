package com.example.ecommerce_backend.controller;


import com.example.ecommerce_backend.dtos.OrderDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.Order;
import com.example.ecommerce_backend.responses.ResponseObject;
import com.example.ecommerce_backend.responses.order.OrderDetailResponse;
import com.example.ecommerce_backend.responses.order.OrderListResponse;
import com.example.ecommerce_backend.responses.order.OrderResponse;
import com.example.ecommerce_backend.services.orders.IOrderService;
import com.example.ecommerce_backend.services.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ) throws Exception {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(String.join(";", errorMessages))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        Order orderResponse = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                        .message("Insert order successfully")
                        .data(OrderResponse.fromOrder(orderResponse))
                        .status(HttpStatus.OK)
                        .build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseObject> getOrdersByUserId(@PathVariable Long userId) throws Exception {
        try {
            // Fetch orders from the service layer
            List<Order> orders = orderService.getOrdersByUserId(userId);

            // Map Order entities to OrderResponse DTOs
            List<OrderResponse> orderResponses = orders.stream()
                    .map(order -> OrderResponse.builder()
                            .id(order.getId())
                            .status(order.getStatus())
                            .orderDate(order.getOrderDate())
                            .fullName(order.getFullName())
                            .phoneNumber(order.getPhoneNumber())
                            .address(order.getAddress())
                            .shippingDate(order.getShippingDate())
                            .totalMoney(order.getTotalMoney())
                            .paymentMethod(order.getPaymentMethod())
                            .userId(order.getUser().getId())
                            .build())
                    .collect(Collectors.toList());

            // Wrap them in OrderListResponse (you can also add total pages if needed)
            OrderListResponse orderListResponse = OrderListResponse.builder()
                    .orders(orderResponses)
                    .totalPages(1) // Replace with actual pagination logic if necessary
                    .build();

            // Return response object with the order list data
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .message("Order details retrieved successfully for user ID: " + userId)
                            .status(HttpStatus.OK)
                            .data(orderListResponse)  // Set the order list as the response data
                            .build()
            );
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseObject.builder()
                                    .message("Failed to find order details: " + e.getMessage())
                                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .data(null)
                                    .build()
                    );
        }
    }

}

