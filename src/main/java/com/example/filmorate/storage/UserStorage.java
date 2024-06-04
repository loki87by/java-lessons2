package com.example.filmorate.storage;

import com.example.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    Optional<User> create(User user);

    User update(User user);

    boolean isInvalidEmail(String emailValue);

}
