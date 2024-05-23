package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.models.Attribute;
import com.example.ecommerce_backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {
}
