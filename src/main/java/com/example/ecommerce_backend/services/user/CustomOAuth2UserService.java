package com.example.ecommerce_backend.services.user;

import com.example.ecommerce_backend.models.SocialAccount;
import com.example.ecommerce_backend.models.User;
import com.example.ecommerce_backend.repositories.SocialAccountRepository;
import com.example.ecommerce_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//    private final UserService userService;
//    private final UserRepository userRepository;
//    private final SocialAccountRepository socialAccountRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        // Create a default OAuth2UserService to load user info
//        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
//        OAuth2User oauth2User = delegate.loadUser(userRequest);
//
//        // Extract user attributes from OAuth2User
//        Map<String, Object> attributes = oauth2User.getAttributes();
//        String email = (String) attributes.get("email");
//        String name = (String) attributes.get("name");
//        String profilePicture = (String) attributes.get("picture");
//        String providerId = (String) attributes.get("sub");
//        String provider = userRequest.getClientRegistration().getRegistrationId();
//
//        // Use UserService to find or create user
//        User user = userService.findOrCreateUser(email, name, profilePicture, providerId);
//
//        // Return OAuth2User with authorities
//        return new DefaultOAuth2User(
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
//                attributes,
//                "email"
//        );
//    }
//}

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService  {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user =  super.loadUser(userRequest);
        return new User();
    }

}
