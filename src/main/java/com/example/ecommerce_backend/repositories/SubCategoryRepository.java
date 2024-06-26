package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.models.Order;
import com.example.ecommerce_backend.models.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    List<SubCategory>findByCategoryId(Long categoryId);
}
