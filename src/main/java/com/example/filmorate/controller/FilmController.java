package com.example.filmorate.controller;

import com.example.filmorate.model.Film;
import com.example.filmorate.service.FilmService;
import com.example.filmorate.storage.FilmStorage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public HashMap<Integer, Film> findAll() {
        log.debug("Текущее количество фильмов: {}", filmStorage.findAll().size());
        return filmStorage.findAll();
    }

    @PostMapping(value = "/films")
    public ResponseEntity<?> create(@RequestBody Film film) {
        List<Object> createFilmRes = filmStorage.create(film);

        if (createFilmRes.getFirst() instanceof Film) {
            log.debug("Данные фильма: {} сохранены", createFilmRes.getFirst());
            return ResponseEntity.ok(createFilmRes.getFirst());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFilmRes);
        }
    }

    @PutMapping(value = "/films")
    public ResponseEntity<?> update(@RequestBody Film film) {
        List<Object> updateFilmRes = filmStorage.update(film);

        if (updateFilmRes.getFirst() instanceof Film) {
            log.debug("Данные фильма: {} сохранены", updateFilmRes.getFirst());
            return ResponseEntity.ok(updateFilmRes.getFirst());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updateFilmRes);
        }
    }

    @PutMapping("/films/{id}/like/{userId}")
    public ResponseEntity<?> like(@PathVariable Integer id, @PathVariable Integer userId) {
        String response = filmService.like(id, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public ResponseEntity<?> dislike(@PathVariable Integer id, @PathVariable Integer userId) {
        String response = filmService.dislike(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostPopular(
                                     @RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmService.getMostPopular(count);
    }
}
