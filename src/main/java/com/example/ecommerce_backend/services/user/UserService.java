package com.example.ecommerce_backend.services.user;

import com.example.ecommerce_backend.components.JwtTokenUtils;
import com.example.ecommerce_backend.dtos.UpdateUserDTO;
import com.example.ecommerce_backend.dtos.UserDTO;
import com.example.ecommerce_backend.dtos.UserLoginDTO;
import com.example.ecommerce_backend.exception.DataAlreadyExistsException;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.exception.ExpiredTokenException;
import com.example.ecommerce_backend.models.Role;
import com.example.ecommerce_backend.models.SocialAccount;
import com.example.ecommerce_backend.models.User;
import com.example.ecommerce_backend.repositories.RoleRepository;
import com.example.ecommerce_backend.repositories.SocialAccountRepository;
import com.example.ecommerce_backend.repositories.UserRepository;
import com.example.ecommerce_backend.services.EmailService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.example.ecommerce_backend.models.Role.USER;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    @Value("${jwt.secretKey}") // Đọc khóa bí mật từ cấu hình
    private String secretKey;

    @Override
    public User createUser(UserDTO userDTO) throws DataNotFoundException  {
        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new DataNotFoundException("Vai trò không tồn tại"));
        // Convert từ userDTO => user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .active(true)
                .build();
        newUser.setRole(role);

        String password = userDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setPassword(encodedPassword);

        return userRepository.save(newUser);
    }


    @Override
    public boolean VerificationUser (UserDTO userDTO) throws DataAlreadyExistsException {
        if (!userDTO.getEmail().isBlank() && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DataAlreadyExistsException("Email already exists");
        }
        if (!userDTO.getPhoneNumber().isBlank() && userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new DataAlreadyExistsException("Phone number already exists");
        }

        return true;
    }

    @Override
    public User findOrCreateUser(String email, String name, String profilePicture, String providerId) throws Exception {
        // Tìm người dùng theo email hoặc tạo mới nếu không tìm thấy
        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new DataNotFoundException("Vai trò không tồn tại"));
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(name);
            newUser.setRole(role);
            newUser.setProfilePicture(profilePicture);
            newUser.setActive(true);
            // Lưu người dùng mới vào cơ sở dữ liệu
            return userRepository.save(newUser);
        });

        // Xác định loại nhà cung cấp OAuth2
        String provider = "google";

        // Tìm tài khoản xã hội theo provider và providerId, hoặc tạo mới nếu không tìm thấy
        socialAccountRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    SocialAccount socialAccount = new SocialAccount();
                    socialAccount.setProvider(provider);
                    socialAccount.setProviderId(providerId);
                    socialAccount.setName(name);
                    socialAccount.setEmail(email);
                    socialAccount.setUser(user);
                    // Lưu tài khoản xã hội mới vào cơ sở dữ liệu
                    return socialAccountRepository.save(socialAccount);
                });

        return user;
    }

    @Transactional
    @Override
    public User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        // Find the existing user by userId
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if (updatedUserDTO.getFullName() != null && !updatedUserDTO.getFullName().trim().isEmpty()) {
            existingUser.setFullName(updatedUserDTO.getFullName());
        }

        // Check if the phone number is being changed and if it already exists for another user
        if (updatedUserDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updatedUserDTO.getPhoneNumber());
        }

        if (updatedUserDTO.getAddress() != null) {
            existingUser.setAddress(updatedUserDTO.getAddress());
        }

        // Save the updated user
        return userRepository.save(existingUser);
    }

    @Override
    public String login(String email, String password, Long roleId) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Email not found ");

        }

        User existingUser = optionalUser.get();
        if(!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }
        if(!optionalUser.get().isActive()) {
            throw new DataNotFoundException("user is locker");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email, password,
                existingUser.getAuthorities()
        );

        //authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);

    }



    @Override
    public User getUserById(long id) {
        // Implement get user by ID logic here
        return null;
    }

    @Override
    public Page<User> findAll(String keyword, Pageable pageable) throws Exception {
        // Implement find all users with keyword logic here
        return null;
    }


    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
//        if(jwtTokenUtil.isTokenExpired(token)) {
//            throw new ExpiredTokenException("Token is expired");
//        }
        String email = jwtTokenUtil.extractClaim(token, Claims::getSubject);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found");
        }
    }

    @Async
    @Override
    public void sendVerificationEmail(UserDTO userDTO, HttpServletResponse response) {
        // Generate a unique token for verification
        try {
            String otp = String.format("%06d", new Random().nextInt(999999));
            ZonedDateTime tokenExpiryTime = ZonedDateTime.now(ZoneId.systemDefault()).plusSeconds(120);
            long secondsUntilExpiry = Duration.between(ZonedDateTime.now(ZoneId.systemDefault()), tokenExpiryTime).getSeconds();

            emailService.sendEmail(userDTO.getEmail(), "Email Verification",
                    "Your OTP code is : " + otp);
            redisTemplate.opsForValue().set("otp:" + userDTO.getEmail(), otp, Duration.ofSeconds(secondsUntilExpiry));
        } catch (Exception ex) {
            throw new RuntimeException("An error occurred while sending verification email", ex);
        }
    }
    @Override
    public boolean isOtpValid(String email, String otp) {
            String key = "otp:" + email;

            // Kiểm tra tồn tại của OTP trong Redis
            Object storedOtpObj = redisTemplate.opsForValue().get(key);
            if (storedOtpObj == null) {
                return false;
            }

            String storedOtp = (String) storedOtpObj;

            // Kiểm tra OTP có hợp lệ không
            if (!storedOtp.equals(otp)) {
                return false;
            }

            // Kiểm tra thời gian hết hạn của OTP
            Long ttl = redisTemplate.getExpire(key);
            if (ttl != null && ttl > 0) {
                // OTP còn hiệu lực
                redisTemplate.delete(key);
                return true;
            }

            // OTP đã hết hạn
            redisTemplate.delete(key); // Xóa OTP hết hạn khỏi Redis
            return false;
        }

}
