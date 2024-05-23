package com.example.ecommerce_backend.controller;


import com.example.ecommerce_backend.dtos.SubCategoryDTO;
import com.example.ecommerce_backend.models.SubCategory;
import com.example.ecommerce_backend.responses.ResponseObject;
import com.example.ecommerce_backend.services.subcategory.SubCategoryService;
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
@RequestMapping("${api.prefix}/subcategories")
public class SubCategoryController {

    private final SubCategoryService subcategoryService;


    @GetMapping("")
    public ResponseEntity<ResponseObject>getAllSubCategories( ) {

        List<SubCategory> subCategories = subcategoryService.getAllSubCategories();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get list of categories successfully")
                .status(HttpStatus.OK)
                .data(subCategories)
                .build());
    }
    @GetMapping("/category/{id}")
    public ResponseEntity<ResponseObject>getSubCategoriesByCategoryId( @PathVariable("id") Long categoryId,BindingResult result){

        try {
            List<SubCategory> subCategories = subcategoryService.findByCategoryId(categoryId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Get list of subcategories successfully")
                    .status(HttpStatus.OK)
                    .data(subCategories)
                    .build());
        }
        catch (RuntimeException e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }



    @PostMapping("")
    public ResponseEntity<ResponseObject>createSubCategory(
            @Valid @RequestBody SubCategoryDTO subcategoryDTO , BindingResult result
            )throws Exception {
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
        SubCategory subCategory = subcategoryService.createSubCategory(subcategoryDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create subcategory successfully")
                .status(HttpStatus.OK)
                .data(subCategory)
                .build());

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject>updateSubCategory(
            @PathVariable long id
            ,@Valid @RequestBody SubCategoryDTO subcategoryDTO){

        SubCategory existingsubCategory = subcategoryService.updateSubCategory(id,subcategoryDTO);
        return ResponseEntity.ok().body(ResponseObject
                .builder()
                .message("Update Category successfully")
                .status(HttpStatus.OK)
                .data(existingsubCategory)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject>deleteSubCategory(@PathVariable long id )throws Exception{

        SubCategory existingsubCategory = subcategoryService.deleteSubCategory(id);
        return ResponseEntity.ok().body(ResponseObject
                .builder()
                .message("Delete Category successfully")
                .status(HttpStatus.OK)
                .data(existingsubCategory)
                .build());
    }

}
