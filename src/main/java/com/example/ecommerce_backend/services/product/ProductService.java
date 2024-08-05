package com.example.ecommerce_backend.services.product;

import com.example.ecommerce_backend.dtos.ProductDTO;
import com.example.ecommerce_backend.dtos.ProductImageDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.exception.InvalidParamException;
import com.example.ecommerce_backend.models.*;
import com.example.ecommerce_backend.repositories.*;
import com.example.ecommerce_backend.responses.ProductResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;



@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final SubCategoryRepository subCategoryRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final ProductImageRepository productImageRepository;

    private final AttributeRepository attributeRepository;

    private static String UPLOADS_FOLDER = "uploads";
    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        SubCategory existingsubCategory = subCategoryRepository
                .findById(productDTO.getSubcategory_id())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find category with id: "+productDTO.getSubcategory_id()));

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
                .shop(existingShop)
                .subcategory(existingsubCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Transactional
    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find product with id: "+productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        //Ko cho insert quá 5 ảnh cho 1 sản phẩm
        int size = productImageRepository.findByProductId(productId).size();
        if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException(
                    "Number of images must be <= "
                            +ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        if (existingProduct.getThumbnail() == null ) {
            existingProduct.setThumbnail(newProductImage.getImageUrl());
        }
        productRepository.save(existingProduct);
        return productImageRepository.save(newProductImage);
    }



    @Override
    public Product getProductById(long productId) throws Exception {
        return productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id = " + productId));
    }


    @Override
    public List<Product> getProductByCategoryId(Long categoryId) throws Exception{
        List<Product> productList = productRepository.findProductsByCategoryId(categoryId);

        if (productList.isEmpty()) {
            throw new RuntimeException("Cannot find product with category_id: " + categoryId);
        }
        return productList;
    }

    @Override
    public List<Product> getProductBySubcategoryId(Long categoryId , Long subcategoryId) throws Exception{
        List<Product> productList = productRepository.findProductsBySubcategoryId(categoryId,subcategoryId);

        if (productList.isEmpty()) {
            throw new RuntimeException("Cannot find product with subcategoryId: " + subcategoryId);
        }
        return productList;
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

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    @Override
    public String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        //String uniqueFilename = UUID.randomUUID().toString() + "_" + filename; //old code, not good
        String uniqueFilename = UUID.randomUUID().toString() + "_" + System.nanoTime(); // Convert nanoseconds to microseconds
        // Đường dẫn đến thư mục mà bạn muốn lưu file
        java.nio.file.Path uploadDir = Paths.get(UPLOADS_FOLDER);
        // Kiểm tra và tạo thư mục nếu nó không tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Đường dẫn đầy đủ đến file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

}
