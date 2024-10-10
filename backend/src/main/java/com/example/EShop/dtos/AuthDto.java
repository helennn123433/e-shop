package com.example.EShop.dtos;

import com.example.EShop.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class AuthDto {
    private Long uid;
    private String email;
    private String username;
    private String surname;
    private String role;
    private String token;

    public AuthDto(User user, String token) {
        this.uid = user.getUid();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.surname = user.getSurname();
        this.role = user.getRoles().toString().replace("[", "").replace("]", "");

        this.token = token;
    }
}
