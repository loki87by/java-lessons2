package com.example.filmorate.controller;

import com.example.filmorate.model.Film;
import com.example.filmorate.storage.FilmDBStorage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class SearchController {
    private final FilmDBStorage filmDBStorage;

    @Autowired
    public SearchController(FilmDBStorage filmDBStorage) {
        this.filmDBStorage = filmDBStorage;
    }
    @GetMapping("/search/{finded}")
    public List<Film> findAll(@PathVariable String finded) {
        return filmDBStorage.searchFilms(finded);
    }
}
