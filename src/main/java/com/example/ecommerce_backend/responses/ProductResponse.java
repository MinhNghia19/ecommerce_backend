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
public class ProductResponse {
    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;
    private int totalPages;

    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();

    @JsonProperty("product_attributes")
    private List<ProductAttributeResponse> productAttributes = new ArrayList<>();

    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("subcategory_name")
    private String subcategoryName;
    public static ProductResponse fromProduct(Product product) {
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getProductName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .totalPages(0)
                .shopName(product.getShop().getShopName())
                .subcategoryName(product.getSubcategory().getName())
                .build();

        productResponse.setProductImages(product.getProductImages());
        productResponse.setProductAttributes(convertProductAttributes(product.getProductAttributes()));

        return productResponse;
    }
    // Convert a list of Products to a list of ProductResponses


    private static List<ProductAttributeResponse> convertProductAttributes(List<ProductAttribute> productAttributes) {
        List<ProductAttributeResponse> attributeResponses = new ArrayList<>();
        for (ProductAttribute attribute : productAttributes) {
            attributeResponses.add(ProductAttributeResponse.builder()

                    .productId(attribute.getProduct().getId())
                    .attributeName(attribute.getAttribute().getName())
                    .value(attribute.getValue())

                    .build());
        }
        return attributeResponses;
    }

}
