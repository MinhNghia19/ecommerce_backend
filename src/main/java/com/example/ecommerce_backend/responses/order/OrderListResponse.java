package com.example.ecommerce_backend.responses.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class OrderListResponse {
    private List<OrderResponse> orders;
    private int totalPages;
}
