package com.example.EShop.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderDto {
    private Long id;
    private String title;
    private Long price;
    private int count;
    private String base64Image;
}
