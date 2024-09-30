package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.components.JwtTokenUtils;
import com.example.ecommerce_backend.dtos.UpdateUserDTO;
import com.example.ecommerce_backend.dtos.UserDTO;
import com.example.ecommerce_backend.dtos.UserLoginDTO;
import com.example.ecommerce_backend.models.User;
import com.example.ecommerce_backend.responses.LoginResponse;
import com.example.ecommerce_backend.responses.ResponseObject;
import com.example.ecommerce_backend.responses.SocialAccountResponse;
import com.example.ecommerce_backend.responses.UserResponse;
import com.example.ecommerce_backend.services.EmailService;
import com.example.ecommerce_backend.services.token.ITokenService;
import com.example.ecommerce_backend.services.user.CustomOAuth2UserService;
import com.example.ecommerce_backend.services.user.IUserService;
import com.example.ecommerce_backend.services.user.UserService;
import com.example.ecommerce_backend.services.utils.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {

    private final IUserService userService;
    private final ITokenService tokenService;
    private final CustomOAuth2UserService oauth2UserService;
    private final JwtTokenUtils jwtTokenUtils;
    private final EmailService emaiService;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult result, HttpServletResponse response)
            throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(errorMessages.toString())
                    .build());
        }

        // Validate email and phone number
        if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
            if (userDTO.getPhoneNumber() == null || userDTO.getPhoneNumber().isBlank()) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .data(null)
                        .message("At least email or phone number is required")
                        .build());
            } else if (!ValidationUtils.isValidPhoneNumber(userDTO.getPhoneNumber())) {
                throw new Exception("Invalid phone number");
            }
        } else if (!ValidationUtils.isValidEmail(userDTO.getEmail())) {
            throw new Exception("Invalid email format");
        }

        // Validate password match
        if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("Passwords do not match")
                    .build());
        }
        try {
            userService.VerificationUser(userDTO);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(e.getMessage())
                    .build());
        }
        userService.sendVerificationEmail(userDTO, response);

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Please check your email for OTP verification.")
                .build());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseObject> verifyOtp(@RequestParam String otp,  @RequestBody UserDTO userDTO) throws Exception {
        // Kiểm tra OTP

        String email = userDTO.getEmail();
        boolean isValid = userService.isOtpValid(email, otp);

        if (isValid) {
            // OTP hợp lệ, tiến hành tạo tài khoản
            User user = userService.createUser(userDTO); // Lưu tài khoản người dùng vào cơ sở dữ liệu

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .message("OTP verified and account created successfully.")
                    .data(UserResponse.fromUser(user))
                    .build());
        } else {
            // OTP không hợp lệ hoặc hết hạn
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Invalid or expired OTP.")
                    .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO

    ) throws Exception {
        // Kiểm tra thông tin đăng nhập và sinh token
        String token = userService.login(
                userLoginDTO.getEmail(),
                userLoginDTO.getPassword(),
                userLoginDTO.getRoleId() == null ? 2 : userLoginDTO.getRoleId()
        );

        String refreshToken = tokenService.refreshToken(
                userLoginDTO.getEmail(),
                userLoginDTO.getPassword(),
                userLoginDTO.getRoleId() == null ? 2 : userLoginDTO.getRoleId());
        User userDetail = userService.getUserDetailsFromToken(token);
        LoginResponse loginResponse = LoginResponse.builder()
                .message("LOGIN_SUCCESSFULLY")
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                .id(userDetail.getId())
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Login successfully")
                .data(loginResponse)
                .status(HttpStatus.OK)
                .build());
    }

    @PutMapping("/details/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updatedUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception{
        String extractedToken = authorizationHeader.substring(7);
        User user = userService.getUserDetailsFromToken(extractedToken);
       //  Ensure that the user making the request matches the user  User existingUser = userRepository.findById(userId);
        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User updatedUser = userService.updateUser(userId, updatedUserDTO);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Update user detail successfully")
                        .data(UserResponse.fromUser(updatedUser))
                        .status(HttpStatus.OK)
                        .build()
        );
    }


    @PostMapping("/details")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getUserDetails(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        // Use the getTokenFromCookie method to retrieve the token
        String extractedToken = authorizationHeader.substring(7);
        // Get user details using the extracted token
        User user = userService.getUserDetailsFromToken(extractedToken);

        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Get user's detail successfully")
                        .data(UserResponse.fromUser(user))
                        .status(HttpStatus.OK)
                        .build()
        );
    }


}
