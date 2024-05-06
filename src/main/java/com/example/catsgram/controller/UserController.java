package com.example.catsgram.controller;

import com.example.catsgram.exceptions.UserAlreadyExistException;
import com.example.catsgram.exceptions.InvalidEmailException;
import com.example.catsgram.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class UserController {
    private HashMap<String, User> users = new HashMap<>();
    private  static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users")
    public HashMap<String, User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users;
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) throws UserAlreadyExistException, InvalidEmailException {
        try {
            String email = user.getEmail();

            if (email.isEmpty()) {
                throw new InvalidEmailException("email не может быть пустым");
            }
            if (users.containsKey(email)) {
                throw new UserAlreadyExistException("Пользователь с таким email уже существует");
            } else {
                users.put(email, user);
                log.debug("Данные пользователя: {} сохранены", user);
                return user;
            }
        } catch (NullPointerException e) {
            throw new InvalidEmailException("email является обязательным полем");
        }
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws UserAlreadyExistException, InvalidEmailException {
        try {
            String email = user.getEmail();

            if (email.isEmpty()) {
                throw new InvalidEmailException("email не может быть пустым");
            }
            User currentUser = users.get(email);
            if (currentUser == null) {
                return create(user);
            } else {
                if (user.getBirthdate() != null) {
                    currentUser.setBirthdate(user.getBirthdate());
                }
                if (user.getNickname() != null && !user.getNickname().isEmpty()) {
                    currentUser.setNickname(user.getNickname());
                }
                return user;
            }
        } catch (NullPointerException e) {
            throw new InvalidEmailException("email является обязательным полем");
        }
    }
}
