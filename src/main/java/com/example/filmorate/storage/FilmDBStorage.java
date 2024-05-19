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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    String minDate = "1895-12-28T00:00:00Z";
    public FilmDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Film makeFilms(ResultSet rs) throws SQLException {
        return new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("releaseDate"),
                rs.getInt("duration")
        );
    }
    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs));
    }

    @Override
    public Optional<Film> create(Film film) {
        String errorMessage;

        if (film.getName() == null) {
            errorMessage = "Название обязательно к заполнению.";
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

        if (film.getMpaRating() < 0) {
            System.out.println("film.getMpaRating()Ж "+film.getMpaRating());
            String sqlMpaQuery = "SELECT id, type FROM mpa_rating;";
            List<String> resultList = jdbcTemplate.query(sqlMpaQuery, (rs, rowNum) -> rs.getInt("id") + " - " +
                    rs.getString("type"));
            String listString = String.join(", ", resultList);
            errorMessage = "Mpa-рейтинг должен быть от 1 до 5 из следующего списка: \n" +
                    listString +
                    "\n или 0 если неизвестно";
            throw new ValidationException(errorMessage);
        }

        String sql = "INSERT INTO films (name, description, releaseDate, duration, mpa_rating_id) VALUES (?, ?, ?, ?";

        if (film.getMpaRating() > 0 && film.getMpaRating() < 6) {
            sql += ", ?)";
        } else {
            sql += ", 0)";
        }
        String finalSql = sql;
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(finalSql);
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setString(3, film.getReleaseDate());
            preparedStatement.setInt(4, film.getDuration());

            if (film.getMpaRating() > 0 && film.getMpaRating() < 6) {
                preparedStatement.setInt(5, film.getMpaRating());
            }
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

        if (LocalDate.parse(film.getReleaseDate())
                .atStartOfDay(ZoneId.systemDefault()).toInstant().isBefore(Instant.parse(minDate))) {
            errorMessage = "До 28 декабря 1985 фильмы не выпускались.";
            throw new ValidationException(errorMessage);
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            errorMessage = "Длина описания не может превышать 200 символов, а переданный текст содержит " +
                    film.getDescription().length() + " символа(ов).";
            throw new ValidationException(errorMessage);
        }

        if (film.getName().isEmpty()) {
            errorMessage = "Название обязательно к заполнению.";
            throw new ValidationException(errorMessage);
        }

        if (film.getDuration() <= 0) {
            errorMessage = "Продолжительность должна быть положительной.";
            throw new ValidationException(errorMessage);
        }

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

        if (rowsAffected > 0) {
            String sqlQuery = "SELECT * from films where id = ?;";
            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilms(rs), id).getFirst();
        }
        return null;
    }
}
