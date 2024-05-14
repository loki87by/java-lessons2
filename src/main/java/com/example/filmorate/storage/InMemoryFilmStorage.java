package com.example.filmorate.storage;

import com.example.filmorate.model.Film;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();

    String minDate = "1895-12-28T00:00:00Z";

    @Override
    public HashMap<Integer, Film> findAll() {
        return films;
    }

    @Override
    public List<Object> create(Film film) {
        List<Object> result = new ArrayList<>();
        int id;

        if (!films.isEmpty()) {
            id = films.size() * 13;
        } else {
            id = 1;
        }
        film.setId(id);

        if (film.getName() == null) {
            String errorMessage = "Название обязательно к заполнению.";
            result.add(errorMessage);
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String errorMessage = "Длина описания не может превышать 200 символов, а переданный текст содержит " +
                    film.getDescription().length() + " символа(ов).";
            result.add(errorMessage);
        }

        if (LocalDate.parse(film.getReleaseDate())
                .atStartOfDay(ZoneId.systemDefault()).toInstant().isBefore(Instant.parse(minDate))) {
            String errorMessage = "До 28 декабря 1985 фильмы не выпускались.";
            result.add(errorMessage);
        }

        if (film.getDuration() <= 0) {
            String errorMessage = "Продолжительность должна быть положительной.";
            result.add(errorMessage);
        }

        if (result.isEmpty()) {
            films.put(id, film);
            result.add(film);
        }
        return result;
    }

    @Override
    public List<Object> update(Film film) {
        try {
            int id = film.getId();

            Film currentFilm = films.get(id);

            if (currentFilm == null) {
                return create(film);
            } else {

                if (LocalDate.parse(film.getReleaseDate())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant().isAfter(Instant.parse(minDate))) {
                    currentFilm.setReleaseDate(film.getReleaseDate());
                }

                if (film.getDescription() != null && !film.getDescription().isEmpty()) {
                    currentFilm.setDescription(film.getDescription());
                }

                if (!film.getName().isEmpty()) {
                    currentFilm.setName(film.getName());
                }

                if (film.getDuration() > 0) {
                    currentFilm.setDuration(film.getDuration());
                }

                List<Object> result = new ArrayList<>();
                result.add(currentFilm);
                return result;
            }
        } catch (NullPointerException e) {
            int id;

            if (!films.isEmpty()) {
                id = films.size() * 13;
            } else {
                id = 1;
            }
            film.setId(id);
            return create(film);
        }
    }
}
