package com.example.filmorate.controller;

import com.example.filmorate.model.Film;
import com.example.filmorate.storage.FilmDBStorage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class SearchController {
    private final FilmDBStorage filmDBStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SearchController(FilmDBStorage filmDBStorage, JdbcTemplate jdbcTemplate) {
        this.filmDBStorage = filmDBStorage;
        this.jdbcTemplate = jdbcTemplate;
    }
    @GetMapping("/search/{finded}")
    public List<Film> findAll(@PathVariable String finded) {
        String resSql =
                "select * from films where lower(name) like lower(concat('%', ?, '%')) " +
                        "or lower(description) like lower(concat('%', ?, '%'));";
        return jdbcTemplate.query(resSql, (rs, _) -> filmDBStorage.makeFilms(rs), finded, finded);
    }
}
