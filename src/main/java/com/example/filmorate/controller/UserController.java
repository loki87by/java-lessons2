package com.example.filmorate.controller;

import com.example.filmorate.model.User;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserController {

    private HashMap<Integer, User> users = new HashMap<>();

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    @GetMapping("/users")
    public HashMap<Integer, User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users;
    }

    @PostMapping(value = "/users")
    public ResponseEntity<?> create(@Valid @RequestBody User user) {
        
        List<String> errorMessages = new ArrayList<>();

        int id;
        if (!users.isEmpty()) {
            id = users.size() * 13;
        } else {
            id = 1;
        }
        user.setId(id);

        if (user.getEmail() == null || isValidEmail(user.getEmail())) {
            String errorMessage = "Email обязателен к заполнению и должен соответствовать стандартам.";
            log.error(errorMessage);
            errorMessages.add(errorMessage);
        }

        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            String errorMessage = "Логин обязателен и не может содержать пробелы.";
            log.error(errorMessage);
            errorMessages.add(errorMessage);
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(Instant.now())) {
            String errorMessage = "Пришельцам из будущего доступ запрещен.";
            log.warn(errorMessage);
            errorMessages.add(errorMessage);
        }

        if (!errorMessages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }

        users.put(id, user);
        log.debug("Данные пользователя: {} сохранены", user);
        return ResponseEntity.ok(user);
    }

    @PutMapping(value = "/users")
    public ResponseEntity<?> update(@Valid @RequestBody User user) {
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
                return ResponseEntity.ok(currentUser);
            }
        } catch (NullPointerException e) {
            int id;
            if (!users.isEmpty()) {
                id = users.size() * 13;
            } else {
                id = 1;
            }
            user.setId(id);
            return create(user);
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
                            if (isValidEmail(emailValue)) {
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
