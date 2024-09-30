package com.example.ecommerce_backend.services.product;

import com.example.ecommerce_backend.dtos.ProductDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.Product;
import com.example.ecommerce_backend.models.SubCategory;
import com.example.ecommerce_backend.responses.ProductResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductService {


    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;

    Product getProductById(long id) throws Exception;

//    Page<ProductResponse> getAllProducts( String keyword,PageRequest pageRequest);
//
//    List<ProductResponse> getProducts(
//            String keyword ,PageRequest pageRequest) throws JsonProcessingException;

    Page<ProductResponse> searchProducts(String keyword, Long categoryId, Long subcategoryId, Pageable pageable);

    List<Product> getProductByCategoryId(Long categoryId) throws Exception;

    List<Product> getProductBySubcategoryId(Long subcategoryId) throws Exception;

    List<Product> findProductsByIds(List<Long> productIds);

    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(long id);

    String storeFile(MultipartFile file) throws IOException;
}
