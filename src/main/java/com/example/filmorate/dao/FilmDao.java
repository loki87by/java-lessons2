package com.example.filmorate.dao;

import com.example.filmorate.model.Film;
import com.example.filmorate.model.TypeIdEntity;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface FilmDao {
    List<TypeIdEntity> getAllGenres();

    TypeIdEntity getGenreById(Integer id);

    List<TypeIdEntity> getAllMpa();

    TypeIdEntity getMpaById(Integer id);

    List<Film> findAll();

    Film findCurrent(int id);

    Optional<Film> create(Film film);

    Film update(Film film);

    String deleteFilm(int id);

    String like(int filmId, int userId);

    String dislike(int filmId, int userId);

    List<Film> getMostPopular(int length, int genre, int year, String director);

    List<Film> getCrossFilms(int userId, int friendId);
}
