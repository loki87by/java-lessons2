package com.example.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class User {
    private int id;
    @Email(message = "Неправильный формат email")
    private String email;
    private String login;
    private String name;
    private Instant birthday;

    public User(int id, String email, String login, String name, Instant birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
