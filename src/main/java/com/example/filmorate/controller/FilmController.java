package com.example.filmorate.controller;

import com.example.filmorate.model.Film;
import com.example.filmorate.service.FilmService;
import com.example.filmorate.storage.FilmStorage;

import jakarta.validation.NoProviderFoundException;
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
        return newFilm.orElseThrow(() -> new NoProviderFoundException("User not found"));
        /*List<Object> createFilmRes = filmStorage.create(film);

        if (createFilmRes.getFirst() instanceof Film) {
            //log.debug("Данные фильма: {} сохранены", createFilmRes.getFirst());
            return (Film) createFilmRes.getFirst();
        } else {
            throw new ValidationException(String.valueOf(createFilmRes));
        }*/
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        /*List<Object> updateFilmRes = filmStorage.update(film);

        if (updateFilmRes.getFirst() instanceof Film) {
            //log.debug("Данные фильма: {} сохранены", updateFilmRes.getFirst());
            return (Film) updateFilmRes.getFirst();
        } else {
            throw new ValidationException(String.valueOf(updateFilmRes));
        }*/

        if(film.getId() > 0) {
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

    /*@GetMapping("/films/popular")
    public List<Film> getMostPopular(
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmService.getMostPopular(count);
    }*/
}
