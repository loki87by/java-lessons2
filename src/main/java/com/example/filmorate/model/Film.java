package com.example.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class Film {
    private int id;
    private String name;
    private String description;
    private String releaseDate;
    private Integer duration;
    private int mpaRating = 0;
    private Set<Integer> likes = new HashSet<>();
    private Set<String> genre = new HashSet<>();

    public Film(int id, String name, String description, String releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(String name, String description, String releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(String name, String description, String releaseDate, Integer duration, int mpaRating) {
        this.mpaRating = mpaRating;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
