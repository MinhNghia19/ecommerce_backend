package com.example.ecommerce_backend.services.user;

import com.example.ecommerce_backend.dtos.UserDTO;
import com.example.ecommerce_backend.dtos.UserLoginDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.Role;
import com.example.ecommerce_backend.models.SocialAccount;
import com.example.ecommerce_backend.models.User;
import com.example.ecommerce_backend.repositories.RoleRepository;
import com.example.ecommerce_backend.repositories.SocialAccountRepository;
import com.example.ecommerce_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SocialAccountRepository socialAccountRepository;
    @Override
    public User createUser(UserDTO userDTO) throws DataNotFoundException  {
        if (!userDTO.getPhoneNumber().isBlank() && userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        if (!userDTO.getEmail().isBlank() && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        Role role =roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException(
                       "Vai trò không tồn tại"));
        //convert from userDTO => user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .active(true)
                .build();

        newUser.setRole(role);
        return userRepository.save(newUser);
    }

    @Override
    public User handleGoogleSignIn(UserDTO userDTO) throws DataNotFoundException {
        // Check if the user already exists based on Google Account ID
        Optional<User> existingUser = userRepository.findByGoogleAccountId(userDTO.getGoogleAccountId());
        Role role =roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Vai trò không tồn tại"));
        if (!existingUser.isPresent()) {

            User newUser = User.builder()
                    .googleAccountId(userDTO.getGoogleAccountId())
                    .fullName(userDTO.getFullName())
                    .email(userDTO.getEmail())
                    .build();
            newUser.setRole(role);
            userRepository.save(newUser);

            SocialAccount newSocialAccount = SocialAccount.builder()
                    .provider("google")
                    .providerId(userDTO.getGoogleAccountId())
                    .name(userDTO.getFullName())
                    .email(userDTO.getEmail())
                    .user(newUser)
                    .build();
            socialAccountRepository.save(newSocialAccount);
            // Trả về người dùng mới
            return newUser;
        }

        return existingUser.get();
    }

    @Override
    public String login(UserLoginDTO userLoginDT) throws Exception {
        return null;
    }

    @Override
    public User getUserById(long id) {
        return null;
    }

    @Override
    public Page<User> findAll(String keyword, Pageable pageable) throws Exception {
        return null;
    }
}
