package com.example.filmorate.storage;

import com.example.filmorate.model.Film;
import com.example.filmorate.model.TypeIdEntity;

import jakarta.validation.NoProviderFoundException;
import jakarta.validation.ValidationException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

@Component
public class FilmDBStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<Integer> getGenres(int id) {
        String setGenreSql = "select genre_id from film_genres where film_id = ?;";
        return new HashSet<>(jdbcTemplate.query(setGenreSql, (rs, _) -> rs.getInt("genre_id"), id));
    }

    private List<TypeIdEntity> getTypeIdEntityList(String sql, int id) {
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, _) -> new TypeIdEntity(
                rs.getInt("id"),
                rs.getString("type")), id));
    }

    public List<TypeIdEntity> getAllTypeIdEntity(String table) {
        String getSql = STR."select * from \{table} where id != ?";
        return getTypeIdEntityList(getSql, 0);
    }

    public TypeIdEntity getTypeIdEntityById(Integer id, String table, String[] errorEntityArgs) {
        String getGenresSql = STR."select * from \{table} where id=?";
        try {
            return getTypeIdEntityList(getGenresSql, id).getFirst();
        } catch (NoSuchElementException e) {
            throw new NoProviderFoundException(STR."\{errorEntityArgs[0]} с 'id=\{id}\{errorEntityArgs[1]}.");
        }
    }

    public static int getSqlWithParams(int id, HashMap<String, String> filmParams, String sqlStart, JdbcTemplate jdbcTemplate) {
        List<String> notNullParamsList = new ArrayList<>();
        List<Object> paramValues = new ArrayList<>();

        for (String key : filmParams.keySet()) {

            if (filmParams.get(key) != null) {
                notNullParamsList.add(STR."\{key} = ?");
                paramValues.add(filmParams.get(key));
            }
        }
        String sql = STR."\{sqlStart}\{String.join(", ", notNullParamsList)} WHERE id = ?";
        paramValues.add(id);
        return jdbcTemplate.update(sql, paramValues.toArray());
    }

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
        Integer count =
                Objects.requireNonNull(jdbcTemplate.queryForObject(getLikesSql, Integer.class, rs.getInt("id")));
        film.setLikes(count);
        film.setGenre(getGenres(rs.getInt("id")));
        return film;
    }

    public Set<Integer> genreChecker(Set<Integer> genre, int id) {
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
    public void checkMpaRating(int rating) {

        if (rating < 1 || rating > 5) {
            String sqlMpaQuery = "SELECT id, type FROM mpa_rating;";
            List<String> resultList = jdbcTemplate.query(sqlMpaQuery, (rs, _) ->
                    STR."\{rs.getInt("id")} - \{rs.getString("type")}");
            String listString = String.join(", ", resultList);
            String errorMessage =
                    STR."Mpa-рейтинг должен быть от 1 до 5 из следующего списка: \n\{
                            listString}\n по умолчанию ограничения строжайшие.";
            throw new ValidationException(errorMessage);
        }
    }

}
