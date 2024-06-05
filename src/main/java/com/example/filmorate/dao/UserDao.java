package com.example.filmorate.dao;

import com.example.filmorate.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface UserDao {
    List<User> findAll() ;
    Optional<User> findById (Integer id);
    Optional<User> create(User user);
    User update(User user);
    String removeUser(int id);
}
