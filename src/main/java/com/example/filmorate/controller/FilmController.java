package com.example.filmorate.controller;

import com.example.filmorate.model.Film;

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

    @Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
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
            return ResponseEntity.ok(film);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createFilmRes);
        }
    }

    @PutMapping(value = "/films")
    public ResponseEntity<?> update(@RequestBody Film film) {
        List<Object> updateFilmRes = filmStorage.update(film);

        if (updateFilmRes.getFirst() instanceof Film) {
            log.debug("Данные фильма: {} сохранены", updateFilmRes.getFirst());
            return ResponseEntity.ok(film);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updateFilmRes);
        }
    }
}
