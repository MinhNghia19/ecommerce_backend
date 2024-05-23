package com.example.ecommerce_backend.responses;


import com.example.ecommerce_backend.models.BaseEntity;
import com.example.ecommerce_backend.models.Product;
import com.example.ecommerce_backend.models.ProductImage;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseEntity {
    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;
    // Thêm trường totalPages
    private int totalPages;

    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();

    @JsonProperty("shop_id")
    private Long shopId;

    @JsonProperty("subcategory_id")
    private Long subcategoryId;
    public static ProductResponse fromProduct(Product product) {

        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getProductName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .subcategoryId(product.getSubCategory().getId())
                .productImages(product.getProductImages())
                .totalPages(0)
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }
}
