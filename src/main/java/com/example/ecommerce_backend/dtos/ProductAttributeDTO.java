package com.example.ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttributeDTO {

    @NotBlank(message = "Value is required")
    private String value;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("attribute_id")
    private Long attributeId;

}
