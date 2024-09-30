package com.example.ecommerce_backend.repositories;
import com.example.ecommerce_backend.models.Product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

//    Page<Product> findAll(Pageable pageable);//phân trang

    @Query("SELECT p FROM Product p " +
            "JOIN p.subcategory s " +
            "JOIN s.category c " +
            "WHERE c.id = :categoryId")
    List<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p " +
            "JOIN p.subcategory s " +
            "JOIN s.category c " +
            "WHERE s.id = :subcategoryId")
    List<Product> findProductsBySubcategoryId( @Param("subcategoryId") Long subcategoryId);

    @Query("SELECT p FROM Product p " +
            "JOIN p.subcategory s " +
            "JOIN s.category c " +
            "WHERE (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:subcategoryId IS NULL OR s.id = :subcategoryId) " +
            "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchProducts(
            @Param("categoryId") Long categoryId,
            @Param("subcategoryId") Long subcategoryId,
            @Param("keyword") String keyword,
            Pageable pageable);



    @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
    List<Product> findProductsByIds(@Param("productIds") List<Long> productIds);


}
