package com.example.ecommerce_backend.services.category;

import com.example.ecommerce_backend.dtos.CategoryDTO;
import com.example.ecommerce_backend.models.Category;
import com.example.ecommerce_backend.repositories.CategoryRepository;
import com.example.ecommerce_backend.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryService implements ICategorySevice{

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;


    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category newcategory = Category
                .builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(newcategory) ;
    }

    @Override
    public Category getCategoryById(long id) {

        return categoryRepository.findById(id).orElseThrow(()-> new RuntimeException("Category not found")) ;
    }


    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(long id, CategoryDTO categoryDTO) {
            Category existingCategory = getCategoryById(id);
            existingCategory.setName(categoryDTO.getName());
            categoryRepository.save(existingCategory);
            return existingCategory;


    }

    @Override
    public Category deleteCategory(long id)throws Exception {
//        Category category = categoryRepository.findById(id)
//                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id:"+id));
//
//        List<Product> products = productRepository.findByCategory(category);
//        if (!products.isEmpty()) {
//            throw new IllegalStateException("Cannot delete category with associated products");
//        } else {
//            categoryRepository.deleteById(id);
//            return  category;
//        }
         return null;
    }
}
