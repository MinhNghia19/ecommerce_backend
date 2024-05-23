package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.dtos.CategoryDTO;
import com.example.ecommerce_backend.models.Category;
import com.example.ecommerce_backend.models.Order;
import com.example.ecommerce_backend.models.Product;
import com.example.ecommerce_backend.models.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

//    List<Product> findByCategory(Category category);
}
