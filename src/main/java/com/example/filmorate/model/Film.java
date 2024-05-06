package com.example.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class Film {
    private int id;
    private String name;
    private String description;
    private Instant releaseDate;
    private Integer duration;

    public Film(int id, String name, String description, Instant releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
