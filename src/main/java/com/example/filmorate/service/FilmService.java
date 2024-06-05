package com.example.filmorate.service;

import jakarta.validation.NoProviderFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FilmService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void checkFilm(int filmId) {
        String checkFilmSql = "select count(*) from films where id = ?;";
        Integer filmCounter = Objects.requireNonNull(jdbcTemplate.queryForObject(checkFilmSql, Integer.class, filmId));

        if (filmCounter <= 0) {
            String error = STR."Фильм с id=\{filmId} не найден.";
            throw new NoProviderFoundException(error);
        }
    }

    public void checkUser(int userId) {
        String checkUserSql = "select count(*) from users where id = ?;";
        Integer userCounter = Objects.requireNonNull(jdbcTemplate.queryForObject(checkUserSql, Integer.class, userId));

        if (userCounter <= 0) {
            String error = STR."Пользователь с id=\{userId} не найден.";
            throw new NoProviderFoundException(error);
        }
    }

    public void checkFilmAndUser(int filmId, int userId) {
        checkFilm(filmId);
        checkUser(userId);
    }
}
