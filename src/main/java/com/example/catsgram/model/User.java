package com.example.catsgram.model;

import lombok.*;

@Data
public class User {
    private String id;
    private String username;
    private String nickname;

    public User(String id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }
}
