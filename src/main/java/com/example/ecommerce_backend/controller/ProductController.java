package com.example.ecommerce_backend.controller;


import com.example.ecommerce_backend.dtos.ProductDTO;
import com.example.ecommerce_backend.dtos.ProductImageDTO;
import com.example.ecommerce_backend.models.Product;
import com.example.ecommerce_backend.models.ProductImage;
import com.example.ecommerce_backend.responses.ProductNormalResponse;
import com.example.ecommerce_backend.responses.ProductResponse;
import com.example.ecommerce_backend.responses.ResponseObject;
import com.example.ecommerce_backend.services.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final ProductService productService;




    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getProductById(
            @PathVariable("id") Long productId
    ) throws Exception {
        try {
            Product existingProduct = productService.getProductById(productId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .data(ProductResponse.fromProduct(existingProduct))
                    .message("Get detail product successfully")
                    .status(HttpStatus.OK)
                    .build());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .data(null)
                            .message("Failed to find product: " + e.getMessage())
                            .build());
        }
    }
    @GetMapping("/category/{id}")
    public ResponseEntity<ResponseObject>getProductByCategoryId(@PathVariable("id") Long categoryId) {
        try {
            List<Product> existingProduct = productService.getProductByCategoryId(categoryId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .data(ProductNormalResponse.fromProductList(existingProduct))
                    .message("Get  product successfully")
                    .status(HttpStatus.OK)
                    .build());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .data(null)
                            .message("Failed to find product: " + e.getMessage())
                            .build());
        }
    }
    @GetMapping("/category/{categoryId}/{subcategoryId}")
    public ResponseEntity<ResponseObject>getProductBySubcategoryId(@PathVariable("categoryId") Long categoryId,@PathVariable("subcategoryId") Long subcategoryId) {
        try {
            List<Product> existingProduct = productService.getProductBySubcategoryId(categoryId,subcategoryId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .data(ProductNormalResponse.fromProductList(existingProduct))
                    .message("Get  product successfully")
                    .status(HttpStatus.OK)
                    .build());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .data(null)
                            .message("Failed to find product: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("")
    //POST http://localhost:8088/v1/api/products
    public ResponseEntity<ResponseObject> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result
    ) throws Exception {

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(String.join("; ", errorMessages))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
        try {
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .message("Create new product successfully")
                            .status(HttpStatus.CREATED)
                            .data(ProductResponse.fromProduct(newProduct))
                            .build());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .data(null)
                            .message("Failed to create product: " + e.getMessage())
                            .build());
        }



    }

    @PostMapping(value = "uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //POST http://localhost:8088/v1/api/products
    public ResponseEntity<ResponseObject> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files
    ) throws Exception {
        Product existingProduct = productService.getProductById(productId);
        files = files == null ? new ArrayList<MultipartFile>() : files;
        if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Maximum number of images allowed to upload is exceeded. Please upload up to 5 images.")
                            .build()
            );
        }
        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.getSize() == 0) {
                continue;
            }
            // Kiểm tra kích thước file và định dạng
            if (file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body(ResponseObject.builder()
                                .message("File size is too large. Please upload a file smaller than 10MB.")
                                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                                .build());
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body(ResponseObject.builder()
                                .message("Uploaded file must be an image. Please upload a valid image file.")
                                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                .build());
            }
            // Lưu file và cập nhật thumbnail trong DTO
            String filename = productService.storeFile(file); // Thay thế hàm này với code của bạn để lưu file
            //lưu vào đối tượng product trong DB
            ProductImage productImage = productService.createProductImage(
                    existingProduct.getId(),
                    ProductImageDTO.builder()
                            .imageUrl(filename)
                            .build()
            );
            productImages.add(productImage);
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Upload image successfully")
                .status(HttpStatus.CREATED)
                .data(productImages)
                .build());
    }



}

