package com.example.catsgram.controller;

import com.example.catsgram.exceptions.NotFoundException;
import com.example.catsgram.exceptions.UserAlreadyExistException;
import com.example.catsgram.exceptions.InvalidEmailException;
import com.example.catsgram.model.ErrorResponse;
import com.example.catsgram.model.User;
import com.example.catsgram.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public HashMap<String, User> findAll() {
        log.debug("Текущее количество пользователей: {}", userService.findAll().size());
        return userService.findAll();
    }

    @GetMapping("/users/{userEmail}")
    public User findByEmail(@RequestBody @PathVariable String userEmail) throws ErrorResponse {
        User current = userService.findAll().get(userEmail);

        if (current == null) {
            String errorMessage = "Пользователь с email: " + userEmail + " не найден.";
            log.warn(errorMessage);
            throw ErrorHandler.notFoundException(new NotFoundException(errorMessage));
        }
        log.debug("Нужный пользователь: {}", current);
        return current;
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) throws ErrorResponse {
        String email = user.getEmail();
        String errorText;

        if (email == null || email.isEmpty()) {
            errorText = "email является обязательным полем";
            throw ErrorHandler.invalidEmailException(new InvalidEmailException(errorText));
        }

        if (userService.findAll().containsKey(email)) {
            errorText = "Пользователь с таким email уже существует";
            throw ErrorHandler.userAlreadyExistException(new UserAlreadyExistException(errorText));
        } else {
            log.debug("Данные пользователя: {} сохранены", user);
            return userService.create(user);
        }
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws ErrorResponse {
        String email = user.getEmail();
        String errorText;

        if (email == null || email.isEmpty()) {
            errorText = "email является обязательным полем";
            throw ErrorHandler.invalidEmailException(new InvalidEmailException(errorText));
        }

        if (userService.findAll().get(user.getEmail()) != null) {
            log.debug("Данные пользователя: {} обновлены", user);
        } else {
            log.debug("Данные пользователя: {} сохранены", user);
        }
        return userService.update(user);
    }
}
