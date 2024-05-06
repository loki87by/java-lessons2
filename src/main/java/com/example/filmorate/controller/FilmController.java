package com.example.filmorate.controller;

import com.example.filmorate.model.Film;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class FilmController {

    private HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public HashMap<Integer, Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @PostMapping(value = "/films")
    public ResponseEntity<?> create(@RequestBody Film film) {
        List<String> errorMessages = new ArrayList<>();

        int id;
        if (!films.isEmpty()) {
            id = films.size() * 13;
        } else {
            id = 1;
        }
        film.setId(id);

        if (film.getName() == null) {
            String errorMessage = "Название обязательно к заполнению.";
            log.error(errorMessage);
            errorMessages.add(errorMessage);
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String errorMessage = "Длина описания не может превышать 200 символов, а переданный текст содержит " +
                    film.getDescription().length() + " символа(ов).";
            log.warn(errorMessage);
            errorMessages.add(errorMessage);
        }

        String minDate = "1895-12-28T00:00:00Z";

        if (film.getReleaseDate().isBefore(Instant.parse(minDate))) {
            String errorMessage = "До 28 декабря 1985 фильмы не выпускались.";
            log.warn(errorMessage);
            errorMessages.add(errorMessage);
        }

        if (film.getDuration() <= 0) {
            String errorMessage = "Продолжительность должна быть положительной.";
            log.warn(errorMessage);
            errorMessages.add(errorMessage);
        }

        if (!errorMessages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }

        films.put(id, film);
        log.debug("Данные фильма: {} сохранены", film);
        return ResponseEntity.ok(film);
    }

    @PutMapping(value = "/films")
    public ResponseEntity<?> update(@RequestBody Film film) {
        try {
            int id = film.getId();

            Film currentFilm = films.get(id);

            if (currentFilm == null) {
                return create(film);
            } else {
                currentFilm.setReleaseDate(film.getReleaseDate());

                if (film.getDescription() != null && !film.getDescription().isEmpty()) {
                    currentFilm.setDescription(film.getDescription());
                }

                if (!film.getName().isEmpty()) {
                    currentFilm.setName(film.getName());
                }

                if (film.getDuration() > 0) {
                    currentFilm.setDuration(film.getDuration());
                }

                return ResponseEntity.ok(currentFilm);
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

