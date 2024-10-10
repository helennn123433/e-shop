package com.example.EShop.dtos;

import com.example.EShop.models.CommentImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private int score;
    private Long userId;
    private String username;
    private Long productId;
    private String productTitle;
    private List<CommentImage> images;
}
