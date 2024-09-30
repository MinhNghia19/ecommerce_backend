package com.example.ecommerce_backend.controller;


import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.Category;
import com.example.ecommerce_backend.models.OrderDetail;
import com.example.ecommerce_backend.responses.ResponseObject;
import com.example.ecommerce_backend.responses.order.OrderDetailResponse;
import com.example.ecommerce_backend.responses.order.OrderResponse;
import com.example.ecommerce_backend.services.orderdetails.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;



    // Get all order details for a user by userId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ResponseObject> getOrderDetailsByOrderId(@PathVariable("orderId") Long orderId) {
        try {

            List<OrderDetail> orderDetails = orderDetailService.getOrderDetailByOrderId(orderId);
            List<OrderDetailResponse> orderDetailResponse = OrderDetailResponse.fromOrderDetailList(orderDetails);
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .message("Order details retrieved successfully for order ID: " + orderId)
                            .status(HttpStatus.OK)
                            .data(orderDetailResponse)
                            .build()
            );
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseObject.builder()
                                    .message("Failed to find orderDetails "+e.getMessage())
                                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .data(null)
                                    .build()
                    );
        }
    }
    //lấy ra danh sách các order_details của 1 order nào đó
}
