package com.example.EShop.controllers;

import com.example.EShop.services.DefaultEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class EmailController {
@Autowired
    private final DefaultEmailService defaultEmailService;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestHeader(value = "Authorization", required = false) String token) {
        defaultEmailService.sendEmail(token);
        return ResponseEntity.ok("Email sent successful");
    }
}