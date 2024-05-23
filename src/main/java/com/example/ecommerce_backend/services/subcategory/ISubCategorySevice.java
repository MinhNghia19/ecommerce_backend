package com.example.ecommerce_backend.services.subcategory;

import com.example.ecommerce_backend.dtos.CategoryDTO;
import com.example.ecommerce_backend.dtos.SubCategoryDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.Category;
import com.example.ecommerce_backend.models.SubCategory;

import java.util.List;

public interface ISubCategorySevice {
    SubCategory createSubCategory(SubCategoryDTO subcategoryDTO) throws DataNotFoundException;

    List<SubCategory>findByCategoryId(Long categoryId);
    SubCategory getSubCategoryById(long id);
    List<SubCategory> getAllSubCategories();
    SubCategory updateSubCategory(long id ,SubCategoryDTO subcategoryDTO);
    SubCategory deleteSubCategory(long id) throws Exception;
}
