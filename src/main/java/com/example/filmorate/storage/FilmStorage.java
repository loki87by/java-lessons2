package com.example.filmorate.storage;

import com.example.filmorate.model.Film;
import com.example.filmorate.model.TypeIdEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<TypeIdEntity> getAllMpa();

    TypeIdEntity getMpaById(Integer id);

    Film makeFilms(ResultSet rs) throws SQLException;

    List<Film> findAll();
    Optional<Film> create(Film film);
    Film update(Film film);

    List<TypeIdEntity> getAllGenres();

    TypeIdEntity getGenreById(Integer id);
}
