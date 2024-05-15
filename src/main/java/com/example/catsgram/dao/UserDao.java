package com.example.catsgram.dao;

import com.example.catsgram.model.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findUserById(String id);
    Optional<User> create(User user);
}
