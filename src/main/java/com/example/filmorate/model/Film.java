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
    private String director;
    private Integer duration;
    private int mpaRating = 5;
    private int likes = 0;
    private Set<Integer> genre = new HashSet<>();

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
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpaRating = mpaRating;
    }

    public Film(int id, String name, String description, String releaseDate, Integer duration, int mpaRating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpaRating = mpaRating;
    }

    public Film(int id, String name, String description, String director, String releaseDate, Integer duration, int mpaRating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.director = director;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpaRating = mpaRating;
    }
}
