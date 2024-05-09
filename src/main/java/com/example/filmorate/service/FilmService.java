package com.example.filmorate.service;

import com.example.filmorate.model.Film;
import com.example.filmorate.storage.FilmStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void like(int filmId, int userId) {
        Film current = filmStorage.findAll().get(filmId);

        if (current != null) {
            current.getLikes().add(userId);
        }
    }

    public void dislike(int filmId, int userId) {
        Film current = filmStorage.findAll().get(filmId);

        if (current != null) {
            current.getLikes().remove(userId);
        }
    }

    public List<Film> getMostPopular() {
        List<Film> liked = new ArrayList<>(filmStorage.findAll().values().stream().filter(x ->
                !x.getLikes().isEmpty()).toList());
        liked.sort(Comparator.comparingInt(film -> film.getLikes().size()));
        return liked.subList(0, 10);
    }
}
