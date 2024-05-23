package com.example.ecommerce_backend.controller;


import com.example.ecommerce_backend.dtos.CategoryDTO;
import com.example.ecommerce_backend.models.Category;
import com.example.ecommerce_backend.responses.ResponseObject;
import com.example.ecommerce_backend.services.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping("")
    public ResponseEntity<ResponseObject>getAllCategories() {

        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get list of categories successfully")
                .status(HttpStatus.OK)
                .data(categories)
                .build());
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject>createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO , BindingResult result
            ){
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(String.join("; ", errorMessages))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
        Category category = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create category successfully")
                .status(HttpStatus.OK)
                .data(category)
                .build());

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject>updateCategory(@PathVariable long id ,@Valid @RequestBody CategoryDTO categoryDTO){

        Category existingCategory = categoryService.updateCategory(id,categoryDTO);
        return ResponseEntity.ok().body(ResponseObject
                .builder()
                .message("Update Category successfully")
                .status(HttpStatus.OK)
                .data(existingCategory)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject>deleteCategory(@PathVariable long id )throws Exception{

        Category existingCategory = categoryService.deleteCategory(id);
        return ResponseEntity.ok().body(ResponseObject
                .builder()
                .message("Delete Category successfully")
                .status(HttpStatus.OK)
                .data(existingCategory)
                .build());
    }

}
