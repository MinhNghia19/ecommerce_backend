package com.example.ecommerce_backend.services.product;

import com.example.ecommerce_backend.dtos.ProductDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.Product;
import com.example.ecommerce_backend.models.SubCategory;
import com.example.ecommerce_backend.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;

    Product getProductById(long id) throws Exception;


    List<Product> getProductByCategoryId(Long categoryId) throws Exception;

    List<Product> getProductBySubcategoryId(Long categoryId , Long subcategoryId) throws Exception;

    public Page<ProductResponse> getAllProducts(String keyword,
                                                Long subcategory_id, PageRequest pageRequest);
    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(long id);

    String storeFile(MultipartFile file) throws IOException;
}
