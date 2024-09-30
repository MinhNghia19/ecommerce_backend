package com.example.ecommerce_backend.services.user;

import com.example.ecommerce_backend.dtos.UpdateUserDTO;
import com.example.ecommerce_backend.dtos.UserDTO;
import com.example.ecommerce_backend.dtos.UserLoginDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.User;
import com.example.ecommerce_backend.responses.ProductResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface IUserService {

    User createUser(UserDTO userDTO) throws DataNotFoundException;

//    User handleGoogleSignIn(UserDTO userDTO) throws DataNotFoundException ;
    String login(String phoneNumber, String password, Long roleId) throws Exception;

    User getUserById(long id);
    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;

    Page<User> findAll(String keyword, Pageable pageable) throws Exception;
    User findOrCreateUser(String email, String name, String profilePicture, String providerId) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    boolean VerificationUser(UserDTO userDTO) throws Exception;

    void sendVerificationEmail(UserDTO userDTO, HttpServletResponse response);

    boolean isOtpValid(String email, String otp);
}
