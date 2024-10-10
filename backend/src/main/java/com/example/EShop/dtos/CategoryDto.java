package com.example.EShop.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CategoryDto {
    private String name = null;
    private String subcategory = null;
    private String subsubcategory = null;
    private Integer order = null;
    private Integer subcategoryOrder = null;
    private Integer subsubcategoryOrder = null;
}