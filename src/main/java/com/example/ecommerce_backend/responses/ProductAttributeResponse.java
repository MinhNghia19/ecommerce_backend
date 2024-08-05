package com.example.ecommerce_backend.responses;



import com.example.ecommerce_backend.models.ProductAttribute;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAttributeResponse {


    private String value;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("attribute_name")
    private String attributeName;

    public static ProductAttributeResponse fromProductAttribute(ProductAttribute productAttribute) {

        return ProductAttributeResponse.builder()
                .value(productAttribute.getValue())
                .attributeName(productAttribute.getAttribute().getName())
                .productId(productAttribute.getProduct().getId())
                .build();
    }
}
