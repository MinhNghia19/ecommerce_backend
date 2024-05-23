package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User , Long> {
}
