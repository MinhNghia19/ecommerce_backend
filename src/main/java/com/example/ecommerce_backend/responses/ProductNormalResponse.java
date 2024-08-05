package com.example.ecommerce_backend.responses;

import com.example.ecommerce_backend.dtos.ProductAttributeDTO;
import com.example.ecommerce_backend.dtos.ProductImageDTO;
import com.example.ecommerce_backend.models.Product;
import com.example.ecommerce_backend.models.ProductAttribute;
import com.example.ecommerce_backend.models.ProductImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductNormalResponse {
    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;


    public static ProductNormalResponse fromProductA(Product product) {
        ProductNormalResponse productNormalResponse = ProductNormalResponse.builder()
                .id(product.getId())
                .name(product.getProductName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .build();
        return productNormalResponse;
    }
    public static List<ProductNormalResponse> fromProductList(List<Product> products) {
        return products.stream()
                .map(ProductNormalResponse::fromProductA)
                .collect(Collectors.toList());
    }
}