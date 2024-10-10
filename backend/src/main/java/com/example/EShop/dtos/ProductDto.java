package com.example.EShop.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String title;
    private String description;
    private int count;
    private Long price;
    private Long previewImageId;
    private Long discountPrice;
    private List<CategoryDto> categories;
    private List<CommentDto> comments;
    private String base64Image;

}
