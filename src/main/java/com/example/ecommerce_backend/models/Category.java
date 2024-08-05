package com.example.ecommerce_backend.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",length = 100,nullable = false)
    private String name;

//    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
//    private List<SubCategory> subCategories;


}
