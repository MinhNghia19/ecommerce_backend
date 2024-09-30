package com.example.ecommerce_backend.responses.order;

import com.example.ecommerce_backend.models.OrderDetail;
import com.example.ecommerce_backend.models.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {

    @JsonProperty("id")
    private Long id; // Use productId instead of Product object

    @JsonProperty("order_id")
    private Long orderId; // Use productId instead of Product object

    @JsonProperty("product_id")
    private Long productId; // Use productId instead of Product object

    @JsonProperty("product_name")
    private String productName; // Use productId instead of Product object


    @JsonProperty("product_thumbnail")
    private String productThumbnail; // Use productId instead of Product object


    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("price")
    private double price;

    @JsonProperty("total_money")
    private double totalMoney;

    @JsonProperty("attributes")
    private String attributes;

    // Static method to convert OrderDetail to OrderDetailResponse
    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .productId(orderDetail.getProduct().getId()) // Assume you get the product ID from Product
                .productName(orderDetail.getProduct().getProductName())
                .productThumbnail(orderDetail.getProduct().getThumbnail())
                .quantity(orderDetail.getNumberOfProducts())
                .price(orderDetail.getPrice())
                .totalMoney(orderDetail.getTotalMoney())
                .attributes(orderDetail.getAttributes())
                .build();
    }

    // Static method to convert a list of OrderDetail to a list of OrderDetailResponse
    public static List<OrderDetailResponse> fromOrderDetailList(List<OrderDetail> orderDetails) {
        return orderDetails.stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .collect(Collectors.toList());
    }
}
