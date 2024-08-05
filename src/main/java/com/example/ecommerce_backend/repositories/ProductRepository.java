package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.dtos.CategoryDTO;
import com.example.ecommerce_backend.dtos.ProductDTO;
import com.example.ecommerce_backend.models.Category;
import com.example.ecommerce_backend.models.Order;
import com.example.ecommerce_backend.models.Product;
import com.example.ecommerce_backend.models.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {



    @Query("SELECT p FROM Product p " +
            "JOIN p.subcategory s " +
            "JOIN s.category c " +
            "WHERE c.id = :categoryId")
    List<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p " +
            "JOIN p.subcategory s " +
            "JOIN s.category c " +
            "WHERE c.id = :categoryId " +
            "AND s.id = :subcategoryId")
    List<Product> findProductsBySubcategoryId(@Param("categoryId") Long categoryId, @Param("subcategoryId") Long subcategoryId);

}
