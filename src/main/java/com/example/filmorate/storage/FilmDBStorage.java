package com.example.filmorate.storage;

import com.example.filmorate.model.Film;
import jakarta.validation.ValidationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    String minDate = "1895-12-28T00:00:00Z";

    public FilmDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film makeFilms(ResultSet rs) throws SQLException {

        Film film = new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("releaseDate"),
                rs.getInt("duration"),
                rs.getInt("mpa_Rating_ID")
        );
        String getLikesSql = "select count(*) from likes where film_id = ?;";
        Integer count = jdbcTemplate.queryForObject(getLikesSql, Integer.class, rs.getInt("id"));
        film.setLikes(count);
        film.setGenre(getGenres(rs.getInt("id")));
        return film;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs));
    }

    private Set<Integer> getGenres (int id) {
        String setGenreSql = "select genre_id from film_genres where film_id = ?;";
        return new HashSet<>(jdbcTemplate.query(setGenreSql, (rs, rowNum) -> rs.getInt("genre_id"), id));
    }

    private Set<Integer> genreChecker(Set<Integer> genre, int id) {
        String oldGenreSql = "delete from film_genres where film_id = ?;";
        jdbcTemplate.update(oldGenreSql, id);
        for (int genreId : genre) {

            if (genreId > 0 && genreId <= 6) {
                String newGenreSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?);";
                jdbcTemplate.update(newGenreSql, id, genreId);
            }
        }

        return getGenres(id);
    }

    private void checkMpaRating(int rating) {

        if (rating < 1 || rating > 5) {
            String sqlMpaQuery = "SELECT id, type FROM mpa_rating;";
            List<String> resultList = jdbcTemplate.query(sqlMpaQuery, (rs, rowNum) -> rs.getInt("id") + " - " +
                    rs.getString("type"));
            String listString = String.join(", ", resultList);
            String errorMessage = "Mpa-рейтинг должен быть от 1 до 5 из следующего списка: \n" +
                    listString +
                    "\n по умолчанию ограничения строжайшие.";
            throw new ValidationException(errorMessage);
        }
    }

    @Override
    public Optional<Film> create(Film film) {
        String errorMessage;

        if (film.getName() == null || film.getName().isEmpty()) {
            errorMessage = "Название обязательно к заполнению и не может быть пустым.";
            throw new ValidationException(errorMessage);
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            errorMessage = "Длина описания не может превышать 200 символов, а переданный текст содержит " +
                    film.getDescription().length() + " символа(ов).";
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

        checkMpaRating(film.getMpaRating());

        String sql = "INSERT INTO films (name, description, releaseDate, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";

        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setString(3, film.getReleaseDate());
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, film.getMpaRating());
            return preparedStatement;
        });

        if (rowsAffected > 0) {
            String sqlQuery = "SELECT max(id) as id from films;";
            Integer id = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
            if (id != null) {
                film.setId(id);
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
            errorMessage = "Длина описания не может превышать 200 символов, а переданный текст содержит " +
                    film.getDescription().length() + " символа(ов).";
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

        checkMpaRating(film.getMpaRating());

        HashMap<String, String> filmParams = new HashMap<>();

        filmParams.put("name", film.getName());
        filmParams.put("description", film.getDescription());
        filmParams.put("releaseDate", film.getReleaseDate());
        filmParams.put("duration", String.valueOf(film.getDuration()));
        filmParams.put("mpa_rating_id", String.valueOf(film.getMpaRating()));
        String sqlStart = "UPDATE films SET ";
        List<String> notNullParamsList = new ArrayList<>();
        List<Object> paramValues = new ArrayList<>();

        for (String key : filmParams.keySet()) {

            if (filmParams.get(key) != null) {
                notNullParamsList.add(key + " = ?");
                paramValues.add(filmParams.get(key));
            }
        }
        String sql = sqlStart + String.join(", ", notNullParamsList) + " WHERE id = ?";
        paramValues.add(id);
        int rowsAffected = jdbcTemplate.update(sql, paramValues.toArray());

        if (!film.getGenre().isEmpty()) {
            rowsAffected += genreChecker(film.getGenre(), id).size();
        }

        if (rowsAffected > 0) {
            String sqlQuery = "SELECT * from films where id = ?;";
            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilms(rs), id).getFirst();
        }
        return null;
    }
}
