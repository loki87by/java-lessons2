package com.example.filmorate.controller;

import com.example.filmorate.model.User;
import com.example.filmorate.storage.UserStorage;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserController {
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping("/users")
    public HashMap<Integer, User> findAll() {
        log.debug("Текущее количество пользователей: {}", userStorage.findAll().size());
        return userStorage.findAll();
    }

    @PostMapping(value = "/users")
    public ResponseEntity<?> create(@Valid @RequestBody User user) {

        List<Object> resultsList = userStorage.create(user);

        if (resultsList.getFirst() instanceof User) {
            log.debug("Данные пользователя: {} сохранены", resultsList.getFirst());
            return ResponseEntity.ok(resultsList.getFirst());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultsList);
        }
    }

    @PutMapping(value = "/users")
    public ResponseEntity<?> update(@Valid @RequestBody User user) {
        List<Object> updateUserList = userStorage.update(user);

        if (updateUserList.getFirst() instanceof User) {
            log.debug("Данные пользователя: {} сохранены", updateUserList.getFirst());
            return ResponseEntity.ok(updateUserList.getFirst());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updateUserList);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {

                    if (error instanceof FieldError fieldError) {

                        if (fieldError.getField().equals("email")) {
                            String emailValue = (String) fieldError.getRejectedValue();

                            if (userStorage.isValidEmail(emailValue)) {
                                return "email: Некорректный формат email.";
                            }
                        }
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getObjectName() + ": " + error.getDefaultMessage();
                })
                .collect(Collectors.toList());
        log.error(String.valueOf(errors));
        return ResponseEntity.badRequest().body(errors);
    }
}
