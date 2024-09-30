package com.example.ecommerce_backend.services.token;


import com.example.ecommerce_backend.components.JwtTokenUtils;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.User;
import com.example.ecommerce_backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService{

    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    private final JwtTokenUtils jwtTokenUtil;

    private final UserRepository userRepository;


    @Override
    public String refreshToken(String email, String password, Long roleId) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User existingUser = optionalUser.get();
        return jwtTokenUtil.generateToken(existingUser);
    }
}
