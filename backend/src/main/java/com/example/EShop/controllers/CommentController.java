package com.example.EShop.controllers;

import com.example.EShop.dtos.CommentDto;
import com.example.EShop.models.Comment;
import com.example.EShop.models.Product;
import com.example.EShop.models.User;
import com.example.EShop.repositories.CommentRepository;
import com.example.EShop.repositories.ProductRepository;
import com.example.EShop.repositories.UserRepository;
import com.example.EShop.services.CommentService;
import com.example.EShop.services.ProductService;
import com.example.EShop.services.UserService;
import com.example.EShop.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final ProductService productService;
    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")

    public ResponseEntity<Comment>addComment(@RequestParam("productId") Long productId,
               @RequestParam("text") String text,
               @RequestParam("score") int score,
               @RequestParam(value = "images", required = false) MultipartFile[] images,
               @RequestHeader("Authorization") String token) throws IOException {

        User user = userRepository.findByUsername(jwtTokenUtils.getUsername(token));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(commentService.addComment(text, score, images, user, product));
    }



    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(commentService.findByProduct(product));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @RequestHeader(value = "Authorization", required = false) String token) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        commentService.delete(comment);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/usersComment/{commentId}")
    public ResponseEntity<?> deleteCommentByUser(@PathVariable Long commentId,
                                                 @RequestHeader(value = "Authorization") String token) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepository.findByUsername(jwtTokenUtils.getUsername(token));
        if (comment.getUser().getId() == user.getId()) {
            commentService.delete(comment);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("That's not your comment.");
        }

    }

    @DeleteMapping("/deleteImage/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCommentImage(@PathVariable Long commentId,
                                                   @RequestParam Long commentImageId,
                                                   @RequestHeader(value = "Authorization") String token) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentService.deleteImage(comment, commentImageId);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/getAllComments")
    public ResponseEntity<List<CommentDto>> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        List<CommentDto> commentDtos = comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(commentDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getOneComment(@PathVariable Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("No comment with this id"));
        CommentDto commentDto = convertToDto(comment);
        return ResponseEntity.ok(commentDto);
    }


    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setScore(comment.getScore());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setProductId(comment.getProduct().getId());
        dto.setProductTitle(comment.getProduct().getTitle());

        // Если изображения не инициализированы, возвращаем пустой список
        if (comment.getImages() == null) {
            dto.setImages(new ArrayList<>());
        } else {
            dto.setImages(comment.getImages());
        }

        return dto;
    }
}
