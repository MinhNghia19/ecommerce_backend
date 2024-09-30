package com.example.ecommerce_backend.controller;


import com.example.ecommerce_backend.dtos.ProductDTO;
import com.example.ecommerce_backend.dtos.ProductImageDTO;
import com.example.ecommerce_backend.models.Product;
import com.example.ecommerce_backend.models.ProductImage;
import com.example.ecommerce_backend.responses.ProductListResponse;
import com.example.ecommerce_backend.responses.ProductResponse;
import com.example.ecommerce_backend.responses.ResponseObject;
import com.example.ecommerce_backend.services.product.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final ProductService productService;


//    @GetMapping("")
//    public ResponseEntity<ProductListResponse> getProducts(
//            @RequestParam(defaultValue = "") String keyword,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int limit
//    ) throws JsonProcessingException {
//        int totalPages = 0;
//        //productRedisService.clear();
//        // Tạo Pageable từ thông tin trang và giới hạn
//        PageRequest pageRequest = PageRequest.of(
//                page, limit,
//                //Sort.by("createdAt").descending()
//                Sort.by("id").ascending()
//        );
////        logger.info(String.format("keyword = %s, category_id = %d, page = %d, limit = %d",
////                keyword, categoryId, page, limit));
//        List<ProductResponse> productResponses = productService
//                .getProducts(keyword,pageRequest);
//
//        if (productResponses!=null && !productResponses.isEmpty()) {
//            totalPages = productResponses.get(0).getTotalPages();
//        }
//        if(productResponses == null) {
//            Page<ProductResponse> productPage = productService
//                    .getAllProducts(keyword,pageRequest);
//            // Lấy tổng số trang
//            totalPages = productPage.getTotalPages();
//            productResponses = productPage.getContent();
//            // Bổ sung totalPages vào các đối tượng ProductResponse
//            for (ProductResponse product : productResponses) {
//                product.setTotalPages(totalPages);
//            }
//        }
//
//        return ResponseEntity.ok(ProductListResponse
//                .builder()
//                .products(productResponses)
//                .totalPages(totalPages)
//                .build());
//    }

    @GetMapping
    public ResponseEntity<ProductListResponse> searchProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "subcategoryId", required = false) Long subcategoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        Page<ProductResponse> products = productService.searchProducts(keyword, categoryId, subcategoryId, pageable);

        // Chuyển đổi Page<ProductResponse> thành List<ProductResponse>
        List<ProductResponse> productList = products.getContent();
        int totalPages = products.getTotalPages();

        // Trả về một đối tượng ProductListResponse chứa danh sách sản phẩm và tổng số trang
        return ResponseEntity.ok(ProductListResponse
                .builder()
                .products(productList)
                .totalPages(totalPages)
                .build());
    }


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
    @GetMapping("/by-ids")
    public ResponseEntity<ResponseObject> getProductsByIds(@RequestParam("ids") String ids) {
        //eg: 1,3,5,7
        // Tách chuỗi ids thành một mảng các số nguyên
        List<Long> productIds = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<Product> products = productService.findProductsByIds(productIds);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(ProductResponse.fromProductList(products))

                .message("Get products successfully")
                .status(HttpStatus.OK)
                .build()
        );
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseObject>getProductByCategoryId(@PathVariable("categoryId") Long categoryId) {
        try {
            List<Product> existingProduct = productService.getProductByCategoryId(categoryId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .data(ProductResponse.fromProductList(existingProduct))
                    .message("Get  products successfully")
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
    @GetMapping("/subcategory/{subcategoryId}")
    public ResponseEntity<ResponseObject>getProductBySubcategoryId(@PathVariable("subcategoryId") Long subcategoryId) {
        try {
            List<Product> existingProduct = productService.getProductBySubcategoryId(subcategoryId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .data(ProductResponse.fromProductList(existingProduct))
                    .message("Get  products successfully")
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
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpeg").toUri()));
                //return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
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

