package com.example.filmorate.controller;

import com.example.filmorate.exception.IdAlreadyExistException;
import com.example.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;


@Slf4j
public class FilmController {

    private HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public HashMap<Integer, Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) throws IdAlreadyExistException {
        try {
            int id = film.getId();

            if (films.containsKey(id)) {
                throw new IdAlreadyExistException("Пользователь с таким id уже существует");
            } else {
                films.put(id, film);
                log.debug("Данные фильма: {} сохранены", film);
                return film;
            }
        } catch (NullPointerException e) {
            int id = films.size()*13;
            film.setId(id);
            films.put(id, film);
            log.debug("Данные фильма: {} сохранены", film);
            return film;
        }
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) throws IdAlreadyExistException {
        try {
            int id = film.getId();

            Film currentFilm = films.get(id);
            if (currentFilm == null) {
                return create(film);
            } else {
                if (film.getReleaseDate() != null) {
                    currentFilm.setReleaseDate(film.getReleaseDate());
                }
                if (film.getDescription() != null && !film.getDescription().isEmpty()) {
                    currentFilm.setDescription(film.getDescription());
                }
                if (film.getName() != null && !film.getName().isEmpty()) {
                    currentFilm.setName(film.getName());
                }
                if (film.getDuration() > 0) {
                    currentFilm.setDuration(film.getDuration());
                }
                return film;
            }
        } catch (NullPointerException e) {
            film.setId(films.size()*13);
            return create(film);
        }
    }
}

