package com.example.filmorate.dao.impls;

import com.example.filmorate.dao.FeedDao;
import com.example.filmorate.dao.FilmDao;
import com.example.filmorate.model.Film;
import com.example.filmorate.model.TypeIdEntity;
import com.example.filmorate.service.FilmService;
import com.example.filmorate.storage.FilmDBStorage;

import jakarta.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDBStorage filmDBStorage;
    private final FilmService filmService;
    private final FeedDao feedDao;
    String minDate = "1895-12-28T00:00:00Z";

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate, FilmService filmService, FilmDBStorage filmDBStorage, FeedDao feedDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmService = filmService;
        this.filmDBStorage = filmDBStorage;
        this.feedDao = feedDao;
    }

    @Override
    public List<TypeIdEntity> getAllGenres() {
        return filmDBStorage.getAllTypeIdEntity("genres");
    }

    @Override
    public TypeIdEntity getGenreById(Integer id) {
        return filmDBStorage.getTypeIdEntityById(id, "genres", new String[]{"Нет записи о жанре", "'"});
    }

    @Override
    public List<TypeIdEntity> getAllMpa() {
        return filmDBStorage.getAllTypeIdEntity("mpa_rating");
    }

    @Override
    public TypeIdEntity getMpaById(Integer id) {
        return filmDBStorage.getTypeIdEntityById(id, "mpa_rating", new String[]{"Mpa-рейтинг ", "' не найден"});
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films;";
        return jdbcTemplate.query(sql, (rs, _) -> filmDBStorage.makeFilms(rs));
    }

    @Override
    public Film findCurrent(int id) {
        String sql = "SELECT * FROM films where id=?;";
        return jdbcTemplate.query(sql, (rs, _) -> filmDBStorage.makeFilms(rs), id).getFirst();
    }

    @Override
    public Optional<Film> create(Film film) {
        String errorMessage;

        if (film.getName() == null || film.getName().isEmpty()) {
            errorMessage = "Название обязательно к заполнению и не может быть пустым.";
            throw new ValidationException(errorMessage);
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            errorMessage =
                    STR."Длина описания не может превышать 200 символов, а переданный текст содержит \{
                            film.getDescription().length()} символа(ов).";
            throw new ValidationException(errorMessage);
        }

        if (LocalDate.parse(film.getReleaseDate())
                .atStartOfDay(ZoneId.systemDefault()).toInstant().isBefore(Instant.parse(minDate))) {
            errorMessage = "До 28 декабря 1985 фильмы не выпускались.";
            throw new ValidationException(errorMessage);
        }

        if (film.getDuration() <= 0) {
            errorMessage = "Продолжительность должна быть положительной.";
            throw new ValidationException(errorMessage);
        }
        filmDBStorage.checkMpaRating(film.getMpaRating());
        String sql = "INSERT INTO films (name, description, director, releaseDate, duration, mpa_rating_id)" +
                " VALUES (?, ?, ?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setString(3, film.getDirector());
            preparedStatement.setString(4, film.getReleaseDate());
            preparedStatement.setInt(5, film.getDuration());
            preparedStatement.setInt(6, film.getMpaRating());
            return preparedStatement;
        });

        if (rowsAffected > 0) {
            String sqlQuery = "SELECT max(id) as id from films;";
            Integer id = jdbcTemplate.queryForObject(sqlQuery, Integer.class);

            if (id != null) {
                film.setId(id);
                feedDao.addToFeed(1, id);
                return Optional.of(film);
            }
        }
        return Optional.empty();
    }

    @Override
    public Film update(Film film) {
        int id = film.getId();
        String errorMessage;

        if (film.getName() != null && film.getName().isEmpty()) {
            errorMessage = "Название обязательно к заполнению и не может быть пустым.";
            throw new ValidationException(errorMessage);
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            errorMessage =
                    STR."Длина описания не может превышать 200 символов, а переданный текст содержит \{
                            film.getDescription().length()} символа(ов).";
            throw new ValidationException(errorMessage);
        }

        if (film.getReleaseDate() != null && LocalDate.parse(film.getReleaseDate())
                .atStartOfDay(ZoneId.systemDefault()).toInstant().isBefore(Instant.parse(minDate))) {
            errorMessage = "До 28 декабря 1985 фильмы не выпускались.";
            throw new ValidationException(errorMessage);
        }

        if (film.getDuration() != null && film.getDuration() <= 0) {
            errorMessage = "Продолжительность должна быть положительной.";
            throw new ValidationException(errorMessage);
        }
        filmDBStorage.checkMpaRating(film.getMpaRating());
        HashMap<String, String> filmParams = new HashMap<>();
        filmParams.put("name", film.getName());
        filmParams.put("description", film.getDescription());
        filmParams.put("director", film.getDirector());
        filmParams.put("releaseDate", film.getReleaseDate());
        filmParams.put("duration", String.valueOf(film.getDuration()));
        filmParams.put("mpa_rating_id", String.valueOf(film.getMpaRating()));
        String sqlStart = "UPDATE films SET ";
        int rowsAffected = FilmDBStorage.getSqlWithParams(id, filmParams, sqlStart, jdbcTemplate, "films");

        if (!film.getGenre().isEmpty()) {
            rowsAffected += filmDBStorage.genreChecker(film.getGenre(), id).size();
        }

        if (rowsAffected > 0) {
            String sqlQuery = "SELECT * from films where id = ?;";
            return jdbcTemplate.query(sqlQuery, (rs, _) -> filmDBStorage.makeFilms(rs), id).getFirst();
        }
        return null;
    }

    @Override
    public String deleteFilm(int id) {
        filmService.checkFilm(id);
        String sql = "delete from films where film_id = ?;";
        jdbcTemplate.update(sql, id);
        feedDao.addToFeed(3, id);
        return STR."Фильм с id=\{id} удалён.";
    }

    @Override
    public String like(int filmId, int userId) {
        filmService.checkFilmAndUser(filmId, userId);
        String likeCounterSql = "select count(*) from likes where film_id = ? and user_id = ?;";
        Integer likeCounter =
                Objects.requireNonNull(jdbcTemplate.queryForObject(likeCounterSql, Integer.class, filmId, userId));

        if (likeCounter == 0) {
            String likeSql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?);";
            jdbcTemplate.update(likeSql, filmId, userId);
            feedDao.addToFeed(10, userId, filmId);
            return "Лайк поставлен.";
        } else {
            return "Вы уже ставили лайк этому фильму ранее.";
        }
    }

    @Override
    public String dislike(int filmId, int userId) {
        filmService.checkFilmAndUser(filmId, userId);
        String likeSql = "delete from likes where film_id = ? and user_id = ?;";
        jdbcTemplate.update(likeSql, filmId, userId);
        feedDao.addToFeed(21, userId, filmId);
        return "Лайк отменен.";
    }

    private Object[] addToArray(Object[] arr, Object addedElement) {
        arr = Arrays.copyOf(arr, arr.length + 1);
        arr[arr.length - 1] = addedElement;
        return arr;
    }

    @Override
    public List<Film> getMostPopular(int length, int genre, int year, String director) {
        String sqlStart =
                "SELECT f.*, COUNT(DISTINCT l.id) AS like_count " +
                        "FROM films f LEFT JOIN likes l ON f.id = l.film_id ";
        String sqlGenreFilter = "";
        Object[] arguments = new Object[]{};

        if (genre > 0 && genre < 7) {
            sqlGenreFilter = "LEFT JOIN film_genres g ON f.id = g.film_id WHERE g.genre_id = ?";
            arguments = addToArray(arguments, genre);
        }

        if (year != 0) {
            sqlGenreFilter += (genre > 0 && genre < 7) ? " AND " : " WHERE ";
            sqlGenreFilter += "YEAR(RELEASEDATE) = ?";
            arguments = addToArray(arguments, year);
        }

        if (!director.isEmpty()) {
            sqlGenreFilter += ((genre > 0 && genre < 7) || (year != 0)) ? " AND " : " WHERE ";
            sqlGenreFilter += "lower(f.director) like lower(concat('%', ?, '%')) ";
            arguments = addToArray(arguments, director);
        }
        arguments = addToArray(arguments, length);
        String sqlEnd = "GROUP BY f.id ORDER BY like_count DESC LIMIT ?;";
        String sqlQuery = sqlStart + sqlGenreFilter + sqlEnd;
        return jdbcTemplate.query(sqlQuery, (rs, _) -> filmDBStorage.makeFilms(rs), arguments);
    }

    @Override
    public List<Film> getCrossFilms(int userId, int friendId) {
        String sql = "select * from films where id in (" +
                "select film_id from (" +
                "select film_id, count(id) from likes where user_id=? or user_id=?" +
                "group by film_id having count(id) = 2)" +
                "where id is not null);";
        return jdbcTemplate.query(sql, (rs, _) -> filmDBStorage.makeFilms(rs), userId, friendId);
    }
}
