package com.example.filmorate.storage;

import com.example.filmorate.model.Film;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    public FilmDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate=jdbcTemplate;
    }
    @Override
    public HashMap<Integer, Film> findAll() {
        return null;
    }

    @Override
    public List<Object> create(Film film) {
        return null;
    }

    @Override
    public List<Object> update(Film film) {
        return null;
    }
}
