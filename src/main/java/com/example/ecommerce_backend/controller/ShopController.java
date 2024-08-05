package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.dtos.ShopDTO;
import com.example.ecommerce_backend.models.Shop;
import com.example.ecommerce_backend.responses.ResponseObject;
import com.example.ecommerce_backend.responses.ShopResponse;
import com.example.ecommerce_backend.services.shop.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/shops")
public class ShopController {

    private final ShopService shopService;
    @PostMapping("")
    public ResponseEntity<ResponseObject> createShop(
            @Valid @RequestBody ShopDTO shopDTO , BindingResult result
            ) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(errorMessages.toString())
                    .build());
        }
        try {
            Shop shop = shopService.createShop(shopDTO);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .data(ShopResponse.fromShop(shop))
                    .message("Shop registration successful")
                    .build());

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .data(null)
                            .message("Failed to create shop: " + e.getMessage())
                            .build());
        }

    }
}
