package com.example.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class Film {
    private int id;
    private String name;
    private String description;
    private Instant releaseDate;
    private Integer duration;
    private Set<Integer> likes = new HashSet<>();

    public Film(int id, String name, String description, Instant releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
