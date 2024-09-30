package com.example.ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


import lombok.*;
;import java.util.List;

@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String name;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Float price;

    private String thumbnail;

    private String description;

    private int stockQuantity;

    @JsonProperty("category_id")
    private Long category_id;

    @JsonProperty("subcategory_id")
    private Long subcategory_id;


    private List<ProductAttributeDTO> attributes;
    private List<ProductImageDTO> images;
}
