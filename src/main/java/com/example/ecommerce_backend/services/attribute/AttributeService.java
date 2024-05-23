package com.example.ecommerce_backend.services.attribute;

import com.example.ecommerce_backend.dtos.AttributeDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.Attribute;
import com.example.ecommerce_backend.models.Category;
import com.example.ecommerce_backend.repositories.AttributeRepository;
import com.example.ecommerce_backend.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeService implements IAttributeSevice{

    private final CategoryRepository categoryRepository;

    private final AttributeRepository attributeRepository;
    @Override
    public Attribute createAttribute(AttributeDTO attributeDTO) throws Exception {
        Category category = categoryRepository.findById(attributeDTO.getCategory_id())
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
        Attribute newattribute = Attribute
                .builder()
                .name(attributeDTO.getName())
                .category(category)
                .build();
        return attributeRepository.save(newattribute) ;
    }

    @Override
    public Attribute getAttributeById(long id) {
        return attributeRepository.findById(id).orElseThrow(()-> new RuntimeException("Attribute not found")) ;
    }

    @Override
    public List<Attribute> getAllAttribute() {
        return attributeRepository.findAll();
    }

    @Override
    public Attribute updateAttribute(long id, AttributeDTO attributeDTO) {
        Attribute attribute = getAttributeById(id);
        attribute.setName(attributeDTO.getName());
        attributeRepository.save(attribute);
        return attribute;
    }

    @Override
    public Attribute deleteAttribute(long id) throws Exception {
        return null;
    }
}
