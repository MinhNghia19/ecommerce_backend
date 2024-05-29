package com.example.ecommerce_backend.responses;


import com.example.ecommerce_backend.models.Role;
import com.example.ecommerce_backend.models.Shop;
import com.example.ecommerce_backend.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("shopname")
    private String shopName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("address")
    private String address;

    @JsonProperty("is_active")
    private boolean active;
//
//    @JsonProperty("user")
//    private User user;


    @JsonProperty("user")
    private UserResponse user;

    public static ShopResponse fromShop(Shop shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .shopName(shop.getShopName())
                .description(shop.getDescription())
                .address(shop.getAddress())
                .active(shop.isActive())
                .user(UserResponse.fromUser(shop.getUser()))
                .build();
    }
}
