package com.example.filmorate.storage;

import com.example.filmorate.model.User;

import java.util.HashMap;
import java.util.List;

public interface UserStorage {
    HashMap<Integer, User> findAll();
    List<Object> create(User user);
    List<Object> update(User user);

    boolean isValidEmail(String emailValue);
}
