package com.example.catsgram.controller;

import com.example.catsgram.exceptions.UserAlreadyExistException;
import com.example.catsgram.exceptions.InvalidEmailException;
import com.example.catsgram.model.User;
import com.example.catsgram.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> findByEmail(@PathVariable String userEmail) {
        User current = userService.findAll().get(userEmail);

        if (current == null) {
            String errorMessage = "Запрашиваемый ресурс не найден.";
            log.warn(errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
        log.debug("Нужный пользователь: {}", current);
        return ResponseEntity.ok(current);
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) throws UserAlreadyExistException, InvalidEmailException {

        try {
            log.debug("Данные пользователя: {} сохранены", user);
            return userService.create(user);
        } catch (NullPointerException e) {
            throw new InvalidEmailException("email является обязательным полем");
        }
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws UserAlreadyExistException, InvalidEmailException {

        try {

            if (userService.findAll().get(user.getEmail()) != null) {
                log.debug("Данные пользователя: {} обновлены", user);
            } else {
                log.debug("Данные пользователя: {} сохранены", user);
            }
            return userService.update(user);
        } catch (NullPointerException e) {
            throw new InvalidEmailException("email является обязательным полем");
        }
    }
}
