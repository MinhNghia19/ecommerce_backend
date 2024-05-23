package com.example.ecommerce_backend.services.subcategory;

import com.example.ecommerce_backend.dtos.SubCategoryDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.Category;
import com.example.ecommerce_backend.models.SubCategory;
import com.example.ecommerce_backend.repositories.CategoryRepository;
import com.example.ecommerce_backend.repositories.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryService implements ISubCategorySevice {

    private final SubCategoryRepository subCategoryRepository;

    private final CategoryRepository categoryRepository;
    @Override
    public SubCategory createSubCategory(SubCategoryDTO subcategoryDTO) throws DataNotFoundException {

        Category category = categoryRepository.findById(subcategoryDTO.getCategory_id())
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
        SubCategory newsubCategory = SubCategory.builder()
                .name(subcategoryDTO.getName())
                .category(category)
                .build();
        return subCategoryRepository.save(newsubCategory);
    }

    @Override
    public List<SubCategory> findByCategoryId(Long categoryId){
        categoryRepository.findById(categoryId)
                .orElseThrow(()->new RuntimeException("Cannot find subcategory with category_id :"+categoryId));
        return subCategoryRepository.findByCategoryId(categoryId);
    }

    @Override
    public SubCategory getSubCategoryById(long id) {

        return subCategoryRepository.findById(id).orElseThrow(()->new RuntimeException("SubCategory not found"));
    }




    @Override
    public List<SubCategory> getAllSubCategories() {
        return subCategoryRepository.findAll();
    }

    @Override
    public SubCategory updateSubCategory(long id, SubCategoryDTO subcategoryDTO) {
        SubCategory subCategory = getSubCategoryById(id);
        subCategory.setName(subcategoryDTO.getName());
        subCategoryRepository.save(subCategory);
        return subCategory;
    }

    @Override
    public SubCategory deleteSubCategory(long id) throws Exception {
        return null;
    }
}