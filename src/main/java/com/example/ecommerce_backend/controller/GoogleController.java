package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.components.JwtTokenUtils;
import com.example.ecommerce_backend.models.User;
import com.example.ecommerce_backend.responses.LoginResponse;
import com.example.ecommerce_backend.responses.ResponseObject;
import com.example.ecommerce_backend.services.user.IUserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import com.example.ecommerce_backend.dtos.GoogleTokenDTO;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class GoogleController {

    private final IUserService userService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final JwtTokenUtils jwtTokenUtil;
    private static final Logger logger = LoggerFactory.getLogger(GoogleController.class);

    @PostMapping("/google/code")
    public ResponseEntity<?> authenticateGoogleUser(@RequestBody GoogleTokenDTO googleTokenDTO,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) {
        try {
            // Lấy token từ cookie
            String existingToken = jwtTokenUtil.getTokenFromCookie(request);

            if (existingToken != null) {
                // Truy xuất thông tin user từ token
                User userDetails = userService.getUserDetailsFromToken(existingToken);

                // Kiểm tra token với userDetails (pass request, token, and userDetails)
                if (jwtTokenUtil.validateToken(existingToken, userDetails)) {
                    // Token hợp lệ, trả về token hiện tại
                    return ResponseEntity.ok().body(
                            ResponseObject.builder()
                                    .message("Token already exists and is valid")
                                    .status(HttpStatus.OK)
                                    .build());
                }
            }
            // Nếu chưa có token hợp lệ, tiến hành xác thực Google ID token
            String idToken = googleTokenDTO.getIdToken();
            logger.info("Received ID Token: {}", idToken);

            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String profilePicture = (String) payload.get("picture");
                String providerId = payload.getSubject();

                // Tìm hoặc tạo mới người dùng dựa trên email
                User user = userService.findOrCreateUser(email, name, profilePicture, providerId);

                // Tạo mới JWT token
                String jwtToken = jwtTokenUtil.generateToken(user);
                String refresToken = jwtTokenUtil.generateRefreshToken(user);

                LoginResponse loginResponse = LoginResponse.builder()
                        .token(jwtToken)
                        .refreshToken(refresToken)
                        .tokenType("Bearer")
                        .username(user.getUsername())
                        .id(user.getId())
                        .roles(user.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                        .build();


                // Trả về token mới
                return ResponseEntity.ok().body(
                        ResponseObject.builder()
                                .message("Authentication successful")
                                .data(loginResponse)
                                .status(HttpStatus.OK)
                                .build());
            } else {
                // ID token không hợp lệ
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseObject.builder()
                                .message("Invalid ID token")
                                .status(HttpStatus.UNAUTHORIZED)
                                .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message(e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }
}