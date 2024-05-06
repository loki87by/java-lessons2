package com.example.filmorate.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Film {
    private final int id;
    private String name;
    private String description;
    private Instant releaseDate;
    private int duration;

    public Film(int id, String name, String description, Instant releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
