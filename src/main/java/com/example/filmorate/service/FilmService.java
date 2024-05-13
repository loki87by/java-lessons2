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

    public String like(int filmId, int userId) {
        Film current = filmStorage.findAll().get(filmId);

        if (current != null) {
            current.getLikes().add(userId);
            return "Лайк поставлен.";
        }
        return "Фильм с id="+filmId+" не найден.";
    }

    public String dislike(int filmId, int userId) {
        Film current = filmStorage.findAll().get(filmId);

        if (current != null) {
            current.getLikes().remove(userId);
            return "Лайк отменен.";
        }
        return "Фильм с id="+filmId+" не найден.";
    }

    public List<Film> getMostPopular(int length) {
        List<Film> liked = new ArrayList<>(filmStorage.findAll().values().stream().filter(x ->
                !x.getLikes().isEmpty()).toList());
        if (liked.size() < length) {
            liked = new ArrayList<>(filmStorage.findAll().values());
        }
        liked.sort(Comparator.comparingInt(film -> film.getLikes().size()));
        liked = liked.reversed();
        int size = Math.min(length, liked.size());
        return liked.subList(0, size);
    }
}
