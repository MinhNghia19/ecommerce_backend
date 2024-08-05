package com.example.ecommerce_backend.services.productattribute;

import com.example.ecommerce_backend.dtos.ProductAttributeDTO;
import com.example.ecommerce_backend.models.ProductAttribute;

public interface IProductAttributeService {

    ProductAttribute createPA(ProductAttributeDTO productAttributeDTO) throws Exception;
}
