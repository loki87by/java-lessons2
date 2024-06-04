package com.example.filmorate.controller;

import com.example.filmorate.dao.FilmDao;
import com.example.filmorate.model.Film;

import jakarta.validation.NoProviderFoundException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmDao filmDao;

    @Autowired
    public FilmController(FilmDao filmDao) {
        this.filmDao = filmDao;
    }

    @GetMapping("")
    public List<Film> findAll() {
        return filmDao.findAll();
    }


    @GetMapping("/{id}")
    public Film findCurrent(@PathVariable Integer id) {
        return filmDao.findCurrent(id);
    }
    @PostMapping(value = "")
    public Film create(@RequestBody Film film) {
        Optional<Film> newFilm = filmDao.create(film);
        return newFilm.orElseThrow(() -> new NoProviderFoundException("Film not found"));
    }

    @PutMapping(value = "")
    public Film update(@RequestBody Film film) {

        if (film.getId() > 0) {
            return filmDao.update(film);
        } else {
            throw new NoProviderFoundException("'id' обязательное поле");
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public String like(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmDao.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public String dislike(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmDao.dislike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopular(
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmDao.getMostPopular(count);
    }

    @GetMapping("/{userId}/common_films/{friendId}")
    public List<Film> getCommonFilms(@PathVariable Integer userId, @PathVariable Integer friendId) {
        return filmDao.getCrossFilms(userId, friendId);
    }
}
