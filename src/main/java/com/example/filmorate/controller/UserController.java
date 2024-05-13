package com.example.filmorate.controller;

import com.example.filmorate.model.User;
import com.example.filmorate.service.UserService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
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

    @GetMapping("/users/{id}")
    public User findById(@RequestBody @PathVariable Integer id) {
        User current = userStorage.findAll().get(id);

        if (current == null) {
            String errorMessage = "Пользователь с id: " + id + " не найден.";
            log.warn(errorMessage);
            //throw ErrorHandler.notFoundException(new NotFoundException(errorMessage));
        }
        log.debug("Нужный пользователь: {}", current);
        return current;
    }

    @GetMapping("/users/{id}/friends")
    public Set<User> findFriendsById(@RequestBody @PathVariable Integer id) {
        User current = userStorage.findAll().get(id);

        if (current == null) {
            String errorMessage = "Пользователь с id: " + id + " не найден.";
            log.warn(errorMessage);
            //throw ErrorHandler.notFoundException(new NotFoundException(errorMessage));
            return null;
        } else {
            Set<Integer> friendsIds = current.getFriends();
            Set<User> friends = new HashSet<>();
            for (int curId: friendsIds) {
                friends.add(userStorage.findAll().get(curId));
            }
            //log.debug("Нужный пользователь: {}", current);
            return friends;
        }
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@RequestBody @PathVariable Integer id, @PathVariable Integer friendId) {
                    String response = userService.addFriend(id, friendId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFriend(@RequestBody @PathVariable Integer id, @PathVariable Integer friendId) {
        String response = userService.removeFriend(id, friendId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}/friends/common/{friendId}")
    public Set<User> getCommonFriends(@RequestBody @PathVariable Integer id, @PathVariable Integer friendId) {
        List<Integer> crossFriendsIds = userService.getCrossFriends(id, friendId);
        Set<User> friends = new HashSet<>();
        for (int curId: crossFriendsIds) {
            friends.add(userStorage.findAll().get(curId));
        }
        return friends;
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
