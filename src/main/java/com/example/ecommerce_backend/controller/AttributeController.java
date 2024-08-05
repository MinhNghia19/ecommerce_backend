package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.dtos.AttributeDTO;
import com.example.ecommerce_backend.models.Attribute;
import com.example.ecommerce_backend.responses.ResponseObject;
import com.example.ecommerce_backend.services.attribute.AttributeService;
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
@RequestMapping("${api.prefix}/attributes")
public class AttributeController {

    private final AttributeService attributeService;

    @PostMapping("")
    public ResponseEntity<ResponseObject> createAttribute(
            @Valid @RequestBody AttributeDTO attributeDTO , BindingResult result
    )throws  Exception{
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
        Attribute attribute = attributeService.createAttribute(attributeDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create category successfully")
                .status(HttpStatus.OK)
                .data(attribute)
                .build());

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject>updateAttribute(@PathVariable long id ,@Valid @RequestBody AttributeDTO attributeDTO){

        Attribute existingAttribute = attributeService.updateAttribute(id,attributeDTO);
        return ResponseEntity.ok().body(ResponseObject
                .builder()
                .message("Update Category successfully")
                .status(HttpStatus.OK)
                .data(existingAttribute)
                .build());
    }
}
