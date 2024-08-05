package com.example.ecommerce_backend.services.productattribute;

import com.example.ecommerce_backend.dtos.ProductAttributeDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.*;
import com.example.ecommerce_backend.repositories.AttributeRepository;
import com.example.ecommerce_backend.repositories.ProductAttributeRepository;
import com.example.ecommerce_backend.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductAttributeService implements IProductAttributeService{
    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final ProductAttributeRepository productAttributeRepository;
    @Override
    public ProductAttribute createPA(ProductAttributeDTO productAttributeDTO) throws Exception {
        Product product = productRepository
                .findById(productAttributeDTO.getProductId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find product with id: "+productAttributeDTO.getProductId()));

        Attribute attribute = attributeRepository
                .findById(productAttributeDTO.getAttributeId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find attribute with id: "+productAttributeDTO.getAttributeId()));

        if (productAttributeRepository.existsByValueAndProductId(productAttributeDTO.getValue(), product.getId())) {
            throw new DataIntegrityViolationException("Value already exists for this product");
        }

        ProductAttribute newPA = ProductAttribute.builder()
                .value(productAttributeDTO.getValue())
                .product(product)
                .attribute(attribute)
                .build();

        return productAttributeRepository.save(newPA);
    }
}
