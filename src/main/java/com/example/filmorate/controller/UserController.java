package com.example.filmorate.controller;

import com.example.filmorate.model.ErrorResponse;
import com.example.filmorate.model.User;
import com.example.filmorate.service.UserService;
import com.example.filmorate.storage.UserStorage;

import jakarta.validation.NoProviderFoundException;
import jakarta.validation.Valid;

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
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        Optional<User> newUser = userStorage.create(user);
        return newUser.orElseThrow(() -> new NoProviderFoundException("User not found"));
    }

    @PutMapping(value = "/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User update(@Valid @RequestBody User user) throws ErrorResponse {

        if(user.getId() > 0) {
            return userStorage.update(user);
        } else {
            throw new NoProviderFoundException("'id' обязательное поле");
        }
    }

    @GetMapping("/users/{id}")
    public User findById(@RequestBody @PathVariable Integer id) {
        List<User> users = userStorage.findAll();
        return users.stream().filter(u -> u.getId() == id).findFirst().orElseThrow(() ->
                new NoProviderFoundException("User not found"));
    }

    @PostMapping(value = "/users/{id}/follow/{friendId}")
    public String follow(@PathVariable int id, @PathVariable int friendId) {
        return userService.follow(id, friendId);
    }

    @GetMapping("/users/{id}/followers")
    public List<User> findFollowersById(@RequestBody @PathVariable Integer id) {
        return userService.findUsersByFriendshipState(id, true);
    }

    @GetMapping("/users/{id}/follows")
    public List<User> findFollowsById(@RequestBody @PathVariable Integer id) {
        return userService.findUsersByFriendshipState(id, false);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> findFriendsById(@RequestBody @PathVariable Integer id) {
        return userService.findFriendsById(id);
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
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.getCrossFriends(id, friendId);/*
        Set<User> friends = new HashSet<>();
        for (int curId : crossFriendsIds) {
            friends.add(userStorage.findAll().get(curId));
        }
        return friends;*/
    }
}
