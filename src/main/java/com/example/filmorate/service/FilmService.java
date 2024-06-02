package com.example.filmorate.service;

import com.example.filmorate.model.Film;
import com.example.filmorate.storage.FilmStorage;

import jakarta.validation.NoProviderFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmService(@Qualifier("filmDBStorage") FilmStorage filmStorage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    private void checkFilmAndUser(int filmId, int userId) {
        String checkFilmSql = "select count(*) from films where id = ?;";
        Integer filmCounter = jdbcTemplate.queryForObject(checkFilmSql, Integer.class, filmId);

        if (filmCounter <= 0) {
            String error = STR."Фильм с id=\{filmId} не найден.";
            throw new NoProviderFoundException(error);
        }
        String checkUserSql = "select count(*) from users where id = ?;";
        Integer userCounter = jdbcTemplate.queryForObject(checkUserSql, Integer.class, userId);

        if (userCounter <= 0) {
            String error = STR."Пользователь с id=\{userId} не найден.";
            throw new NoProviderFoundException(error);
        }
    }

    public String like(int filmId, int userId) {
        checkFilmAndUser(filmId, userId);
        String likeCounterSql = "select count(*) from likes where film_id = ? and user_id = ?;";
        Integer likeCounter = jdbcTemplate.queryForObject(likeCounterSql, Integer.class, filmId, userId);

        if (likeCounter == 0) {
            String likeSql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?);";
            jdbcTemplate.update(likeSql, filmId, userId);
            return "Лайк поставлен.";
        } else {
            return "Вы уже ставили лайк этому фильму ранее.";
        }
    }

    public String dislike(int filmId, int userId) {
        checkFilmAndUser(filmId, userId);
        String likeSql = "delete from likes where film_id = ? and user_id = ?;";
        jdbcTemplate.update(likeSql, filmId, userId);
        return "Лайк отменен.";
    }

    public List<Film> getMostPopular(int length) {
        String sqlQuery =
                "SELECT * FROM films WHERE id IN (" +
                        "SELECT film_id FROM likes GROUP BY film_id HAVING COUNT(DISTINCT user_id) > 0)" +
                        "ORDER BY (SELECT COUNT(DISTINCT user_id) FROM likes WHERE film_id = films.id) DESC LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, (rs, _) -> filmStorage.makeFilms(rs), length);
    }
}
