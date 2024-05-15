package com.example.catsgram.model;

import lombok.*;

import java.time.LocalDate;

@Data
public class Post {
    private Integer id;
    private final User author;
    private final LocalDate creationDate;
    private String description;
    private String photoUrl;

    public Post(User author, String description, String photoUrl, LocalDate creationDate) {
        this.author = author;
        this.creationDate = creationDate;
        this.description = description;
        this.photoUrl = photoUrl;
    }

    public Post(Integer id, User author, LocalDate creationDate, String description, String photoUrl) {
        this.id = id;
        this.author = author;
        this.creationDate = creationDate;
        this.description = description;
        this.photoUrl = photoUrl;
    }
}
