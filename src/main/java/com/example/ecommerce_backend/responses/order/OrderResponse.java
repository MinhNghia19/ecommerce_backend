package com.example.ecommerce_backend.responses.order;

import com.example.ecommerce_backend.models.Order;
import com.example.ecommerce_backend.models.OrderDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    @JsonProperty("note")
    private String note;

    @JsonProperty("order_date")
    private LocalDate orderDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("total_money")
    private double totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("order_details")
    private List<OrderDetailResponse> orderDetails;

    // Static method to create OrderResponse from Order
    public static OrderResponse fromOrder(Order order) {
        return OrderResponse
                .builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .fullName(order.getFullName())
                .phoneNumber(order.getPhoneNumber())
                .email(order.getEmail())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .status(order.getStatus()) // Convert enum to string
                .totalMoney(order.getTotalMoney())
                .shippingMethod(order.getShippingMethod())
                .shippingAddress(order.getShippingAddress())
                .shippingDate(order.getShippingDate())
                .paymentMethod(order.getPaymentMethod())
//                .orderDetails(order.getOrderDetails().stream()
//                        .map(OrderResponse::mapToOrderDetailResponse)
//                        .collect(Collectors.toList())) // Map OrderDetail to OrderDetailResponse
                .build();
    }

    // Helper method to map OrderDetail to OrderDetailResponse
//    private static OrderDetailResponse mapToOrderDetailResponse(OrderDetail orderDetail) {
//        return OrderDetailResponse
//                .builder()
//                .productId(orderDetail.getProduct().getId()) // Assume you get the product ID from Product
//                .productName(orderDetail.getProduct().getProductName())
//                .productThumnail(orderDetail.getProduct().getThumbnail())
//                .quantity(orderDetail.getNumberOfProducts())
//                .price(orderDetail.getPrice())
//                .totalMoney(orderDetail.getTotalMoney())
//                .attributes(orderDetail.getAttributes()) // JSON string of attributes
//                .build();
//    }
}
