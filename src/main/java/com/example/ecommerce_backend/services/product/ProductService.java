package com.example.ecommerce_backend.services.product;

import com.example.ecommerce_backend.dtos.ProductDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.Product;
import com.example.ecommerce_backend.models.Shop;
import com.example.ecommerce_backend.models.SubCategory;
import com.example.ecommerce_backend.repositories.ProductRepository;
import com.example.ecommerce_backend.repositories.ShopRepository;
import com.example.ecommerce_backend.repositories.SubCategoryRepository;
import com.example.ecommerce_backend.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final SubCategoryRepository subCategoryRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        SubCategory existingsubCategory = subCategoryRepository
                .findById(productDTO.getSubcategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find category with id: "+productDTO.getSubcategoryId()));

        Shop existingShop = shopRepository
                .findById(productDTO.getShopId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find shop with id: "+productDTO.getShopId()));

        Product newProduct = Product.builder()
                .productName(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .stockQuantity(productDTO.getStockQuantity())
//                .shop(existingShop)
                .subCategory(existingsubCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long id) throws Exception {
        return null;
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long subcategory_id, PageRequest pageRequest) {
        return null;
    }

    @Override
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        return null;
    }

    @Override
    public void deleteProduct(long id) {

    }
}
