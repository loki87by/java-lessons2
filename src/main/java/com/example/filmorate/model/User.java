package com.example.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private int id = 0;
    @Email(message = "Неправильный формат email")
    private String email;
    private String login;
    private String name;
    private String birthday = null;

    public User(String email, String login, String name, String birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(String email, String login, String name, String birthday, int id) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.id = id;
    }
}
