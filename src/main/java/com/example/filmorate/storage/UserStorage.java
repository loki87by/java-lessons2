package com.example.filmorate.storage;

import com.example.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    HashMap<Integer, User> findAll();

    Optional<User> create(User user);

    List<Object> update(User user);

    boolean isValidEmail(String emailValue);
}
