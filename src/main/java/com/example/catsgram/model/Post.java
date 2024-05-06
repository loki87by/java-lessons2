package com.example.catsgram.model;

import lombok.*;

import java.time.Instant;

@Data
public class Post {
    private final String author;
    private final Instant creationDate = Instant.now();
    private String description;
    private String photoUrl;

    public Post(String author, String description, String photoUrl) {
        this.author = author;
        this.description = description;
        this.photoUrl = photoUrl;
    }
}
