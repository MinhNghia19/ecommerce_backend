package com.example.ecommerce_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttributeDTO {

    @NotBlank(message = "Category's name cannot be empty or contain only spaces")
    private String name;

    private Long category_id;
}
