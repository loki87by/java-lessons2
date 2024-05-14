package com.example.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class User {
    private int id;
    @Email(message = "Неправильный формат email")
    private String email;
    private String login;
    private String name;
    private String birthday = null;
    private Set<Integer> friends = new HashSet<>();

    public User(int id, String email, String login, String name, String birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
