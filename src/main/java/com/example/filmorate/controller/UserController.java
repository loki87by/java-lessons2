package com.example.filmorate.controller;

import com.example.filmorate.model.User;
import com.example.filmorate.service.UserService;
import com.example.filmorate.storage.UserStorage;

import jakarta.validation.NoProviderFoundException;
import jakarta.validation.Valid;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(@Qualifier("userDBStorage") UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping("/users")
    public HashMap<Integer, User> findAll() {
        //log.debug("Текущее количество пользователей: {}", userStorage.findAll().size());
        return userStorage.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        Optional<User> newUser = userStorage.create(user);
        return newUser.orElseThrow(() -> new NoProviderFoundException("User not found"));
    }

    @PutMapping(value = "/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User update(@Valid @RequestBody User user) {
        List<Object> updateUserList = userStorage.update(user);

        if (updateUserList.getFirst() instanceof User) {
            //log.debug("Данные пользователя: {} сохранены", updateUserList.getFirst());
            return (User) updateUserList.getFirst();
        } else {
            throw new ValidationException(String.valueOf(updateUserList));
        }
    }

    @GetMapping("/users/{id}")
    public User findById(@RequestBody @PathVariable Integer id) {
        User current = userStorage.findAll().get(id);

        if (current == null) {
            String errorMessage = "Пользователь с id: " + id + " не найден.";
            //log.warn(errorMessage);
            throw new NoProviderFoundException(errorMessage);
        }
        //log.debug("Нужный пользователь: {}", current);
        return current;
    }

    @GetMapping("/users/{id}/friends")
    public Set<User> findFriendsById(@RequestBody @PathVariable Integer id) {
        User current = userStorage.findAll().get(id);

        if (current == null) {
            String errorMessage = "Пользователь с id: " + id + " не найден.";
            //log.warn(errorMessage);
            throw new NoProviderFoundException(errorMessage);
        } else {
            //Set<Integer> friendsIds = current.getFriends();
            Set<User> friends = new HashSet<>();
            /*for (int curId : friendsIds) {
                friends.add(userStorage.findAll().get(curId));
            }*/
            return friends;
        }
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public String addFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public String removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends/common/{friendId}")
    public Set<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        List<Integer> crossFriendsIds = userService.getCrossFriends(id, friendId);
        Set<User> friends = new HashSet<>();
        for (int curId : crossFriendsIds) {
            friends.add(userStorage.findAll().get(curId));
        }
        return friends;
    }
}
