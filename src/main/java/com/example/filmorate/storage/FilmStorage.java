package com.example.filmorate.storage;

import com.example.filmorate.model.Film;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public interface FilmStorage {
    HashMap<Integer, Film> findAll();
    List<Object> create(Film film);
    List<Object> update(Film film);
}
