package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User , Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByGoogleAccountId(String googleAccountId);

    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}
