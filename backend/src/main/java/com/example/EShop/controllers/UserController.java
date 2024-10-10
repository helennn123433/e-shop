package com.example.EShop.controllers;

import com.example.EShop.dtos.AuthDto;
import com.example.EShop.dtos.UserDto;

import com.example.EShop.repositories.UserRepository;
import com.example.EShop.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import com.example.EShop.models.User;
import com.example.EShop.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<?> auth(@RequestParam(name = "email") String email ,@RequestParam(name = "password") String password) {

        try {
            String token = authService.createAuthToken(email, password);
            User user = userRepository.findByEmail(email);
            return ResponseEntity.ok(new AuthDto(user, token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неправильный логин или пароль");
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestParam(name = "username") String username,
                                           @RequestParam(name = "surname") String surname,
                                           @RequestParam(name = "email") String email,
                                           @RequestParam(name = "password") String password) {
        User user = userService.createNewUser(username, surname, email, password);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername(), user.getEmail()));
    }
}