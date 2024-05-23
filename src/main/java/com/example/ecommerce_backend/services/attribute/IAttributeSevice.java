package com.example.ecommerce_backend.services.attribute;

import com.example.ecommerce_backend.dtos.AttributeDTO;
import com.example.ecommerce_backend.dtos.CategoryDTO;
import com.example.ecommerce_backend.models.Attribute;
import com.example.ecommerce_backend.models.Category;

import java.util.List;

public interface IAttributeSevice {
    Attribute createAttribute(AttributeDTO attributeDTO) throws Exception;

    Attribute getAttributeById(long id);
    List<Attribute> getAllAttribute();
    Attribute updateAttribute(long id ,AttributeDTO attributeDTO);
    Attribute deleteAttribute(long id) throws Exception;
}
