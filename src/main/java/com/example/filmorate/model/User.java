package com.example.filmorate.model;

import lombok.Data;

import java.time.Instant;

@Data
public class User {
    private int id;
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
