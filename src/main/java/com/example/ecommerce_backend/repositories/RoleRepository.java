package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.models.Order;
import com.example.ecommerce_backend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
