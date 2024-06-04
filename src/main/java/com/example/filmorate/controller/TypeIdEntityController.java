package com.example.filmorate.controller;

import com.example.filmorate.dao.FilmDao;
import com.example.filmorate.model.TypeIdEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Component
public class TypeIdEntityController {
    private final FilmDao filmDao;

    @Autowired
    public TypeIdEntityController(FilmDao filmDao) {
        this.filmDao = filmDao;
    }

    @GetMapping("/genres")
    public List<TypeIdEntity> getGenres() {
        return filmDao.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public TypeIdEntity getGenres(@PathVariable Integer id) {
        return filmDao.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<TypeIdEntity> getRatings() {
        return filmDao.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public TypeIdEntity getRatings(@PathVariable Integer id) {
        return filmDao.getMpaById(id);
    }
}
