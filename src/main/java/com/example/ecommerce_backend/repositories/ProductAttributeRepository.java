package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.models.Order;
import com.example.ecommerce_backend.models.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
}
