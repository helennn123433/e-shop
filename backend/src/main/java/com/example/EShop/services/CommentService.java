package com.example.EShop.services;

import com.example.EShop.models.Comment;
import com.example.EShop.models.CommentImage;
import com.example.EShop.models.Product;
import com.example.EShop.models.User;
import com.example.EShop.repositories.CommentRepository;
import com.example.EShop.repositories.ProductRepository;
import com.example.EShop.repositories.UserRepository;
import com.example.EShop.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final ProductRepository productRepository;


    public List<Comment> findByProduct(Product product) {
        return commentRepository.findByProduct(product);
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    public void deleteImage(Comment comment, Long id) {

        List<CommentImage> images = comment.getImages();

        if (images != null && !images.isEmpty()) {
            images.removeIf(image -> image.getId().equals(id));
            comment.setImages(images);
            commentRepository.save(comment);
        } else {
            throw new RuntimeException("У комментария нет изображений для удаления.");
        }
    }

    public Comment addComment(String text, int score, MultipartFile[] images, User user, Product product) throws IOException {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setProduct(product);
        comment.setText(text);
        comment.setScore(score);

        if (comment.getImages().isEmpty()) {
            comment.setImages(new ArrayList<>());
        }

        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = saveImage(image);
                    CommentImage commentImage = new CommentImage();
                    commentImage.setImageUrl(imageUrl);
                    commentImage.setComment(comment);
                    commentImage.setBytes(image.getBytes());
                    comment.getImages().add(commentImage);
                }
            }
        }
        return commentRepository.save(comment);
    }

    private String saveImage(MultipartFile image) throws IOException {
        String directory = "images/";
        Path imageDirectory = Paths.get(directory);

        if (!Files.exists(imageDirectory)) {
            Files.createDirectories(imageDirectory); // Создаем директорию, если ее нет
        }

        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path imagePath = imageDirectory.resolve(fileName);

        Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

        return "/images/" + fileName;
    }
}
