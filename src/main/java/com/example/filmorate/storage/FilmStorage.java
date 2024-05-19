package com.example.filmorate.storage;

import com.example.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film makeFilms(ResultSet rs) throws SQLException;

    List<Film> findAll();
    Optional<Film> create(Film film);
    Film update(Film film);
}
