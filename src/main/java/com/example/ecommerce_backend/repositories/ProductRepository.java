package com.example.ecommerce_backend.repositories;

import com.example.ecommerce_backend.dtos.CategoryDTO;
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

//    List<Product> findByCategory(Category category);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImages WHERE p.id = :productId")
    Optional<Product> getDetailProduct(@Param("productId") Long productId);

}
