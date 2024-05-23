package com.example.ecommerce_backend.services.category;

import com.example.ecommerce_backend.dtos.CategoryDTO;
import com.example.ecommerce_backend.models.Category;

import java.util.List;

public interface ICategorySevice {
    Category createCategory(CategoryDTO categoryDTO);

    Category getCategoryById(long id);
    List<Category> getAllCategories();
    Category updateCategory(long id ,CategoryDTO categoryDTO);
    Category deleteCategory(long id) throws Exception;
}
