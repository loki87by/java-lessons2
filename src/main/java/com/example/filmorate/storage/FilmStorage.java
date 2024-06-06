package com.example.filmorate.storage;

import com.example.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();

    Optional<Film> create(Film film);

    Optional<Film> update(Film film);
}
