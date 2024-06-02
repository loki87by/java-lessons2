package com.example.filmorate.controller;

import com.example.filmorate.model.Feedback;
import com.example.filmorate.model.Film;
import com.example.filmorate.model.TypeIdEntity;
import com.example.filmorate.service.FilmService;
import com.example.filmorate.storage.FilmStorage;

import jakarta.validation.NoProviderFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(@Qualifier("filmDBStorage") FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        //log.debug("Текущее количество фильмов: {}", filmStorage.findAll().size());
        return filmStorage.findAll();
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        Optional<Film> newFilm = filmStorage.create(film);
        return newFilm.orElseThrow(() -> new NoProviderFoundException("Film not found"));
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {

        if (film.getId() > 0) {
            return filmStorage.update(film);
        } else {
            throw new NoProviderFoundException("'id' обязательное поле");
        }
    }

    @PutMapping("/films/{id}/like/{userId}")
    public String like(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.like(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public String dislike(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.dislike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostPopular(
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmService.getMostPopular(count);
    }

    @GetMapping("/genres")
    public List<TypeIdEntity> getGenres() {
        return filmStorage.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public TypeIdEntity getGenres(@PathVariable Integer id) {
        return filmStorage.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<TypeIdEntity> getRatings() {
        return filmStorage.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public TypeIdEntity getRatings(@PathVariable Integer id) {
        return filmStorage.getMpaById(id);
    }

    @PostMapping("/films/{id}/comment/{userId}")
    public Optional<Feedback> setComment(@PathVariable Integer id,
                                         @PathVariable Integer userId,
                                         @RequestParam(required = false, defaultValue = "0") int rate,
                                         @RequestParam String content) {
        if (rate < 0 || rate > 10) {
            throw new ValidationException("Оценка может быть от 1 до 10 или 0 если без оценки.");
        }
        return filmService.comment(id, userId, content, rate);
    }
}
