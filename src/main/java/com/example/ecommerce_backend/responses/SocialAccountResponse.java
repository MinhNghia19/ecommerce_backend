package com.example.ecommerce_backend.responses;

import com.example.ecommerce_backend.models.Role;
import com.example.ecommerce_backend.models.SocialAccount;
import com.example.ecommerce_backend.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialAccountResponse {

    private String provider;
    private String name;
    private String email;

    @JsonProperty("user_id")
    private User userId;
    public static SocialAccountResponse fromSocialAccountResponse(SocialAccount socialAccount) {
        return SocialAccountResponse.builder()
                .name(socialAccount.getName())
                .email(socialAccount.getEmail())
                .provider(socialAccount.getProvider())
                .userId(socialAccount.getUser())
                .build();
    }
}
