package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.models.Category;
import com.example.ecommerce_backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
