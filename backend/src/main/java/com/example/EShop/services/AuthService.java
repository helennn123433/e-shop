package com.example.EShop.services;


import com.example.EShop.models.CustomUserDetails;
import com.example.EShop.repositories.UserRepository;
import com.example.EShop.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public String createAuthToken(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRepository.findByEmail(email).getUsername(), password));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Неправильный логин или пароль");
        }
        CustomUserDetails customUserDetails = userService.loadUserByUsername(userRepository.findByEmail(email).getUsername());
        return jwtTokenUtils.generateToken(customUserDetails);

    }
}