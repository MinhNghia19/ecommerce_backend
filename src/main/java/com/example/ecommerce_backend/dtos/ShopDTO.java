package com.example.ecommerce_backend.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopDTO {

    @NotEmpty(message = "Shop's name cannot be empty")
    @JsonProperty("shopname")
    private String shopName;


    @Min(value=1, message = "User's ID must be > 0")
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("description")
    private String description;

    @NotEmpty(message = "Address cannot be empty")
    @JsonProperty("address")
    private String address;
}
