package com.example.filmorate.storage;

import com.example.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();
    User makeUsers(ResultSet rs) throws SQLException;

    Optional<User> create(User user);

    User update(User user);

    boolean isInvalidEmail(String emailValue);

    Optional<User> findById(Integer id);
}
