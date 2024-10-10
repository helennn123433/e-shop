package com.example.EShop.repositories;

import com.example.EShop.models.Comment;
import com.example.EShop.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProduct(Product product);
    List<Comment> findAll();
}
