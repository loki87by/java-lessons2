package com.example.filmorate.controller;

import com.example.filmorate.model.User;
import com.example.filmorate.exception.IdAlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
public class UserController {

    private HashMap<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public HashMap<Integer, User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users;
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) throws IdAlreadyExistException {
        try {
            int id = user.getId();

            if (users.containsKey(id)) {
                throw new IdAlreadyExistException("Пользователь с таким id уже существует");
            } else {
                users.put(id, user);
                log.debug("Данные пользователя: {} сохранены", user);
                return user;
            }
        } catch (NullPointerException e) {
            int id = users.size()*13;
            user.setId(id);
            users.put(id, user);
            log.debug("Данные пользователя: {} сохранены", user);
            return user;
        }
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws IdAlreadyExistException {
        try {
            int id = user.getId();

            User currentUser = users.get(id);
            if (currentUser == null) {
                return create(user);
            } else {
                if (user.getBirthday() != null) {
                    currentUser.setBirthday(user.getBirthday());
                }
                if (user.getLogin() != null && !user.getLogin().isEmpty()) {
                    currentUser.setLogin(user.getLogin());
                }
                if (user.getName() != null && !user.getName().isEmpty()) {
                    currentUser.setName(user.getName());
                }
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    currentUser.setEmail(user.getEmail());
                }
                return user;
            }
        } catch (NullPointerException e) {
            user.setId(users.size()*13);
            return create(user);
        }
    }
}
