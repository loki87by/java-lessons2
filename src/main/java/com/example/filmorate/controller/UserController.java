package com.example.filmorate.controller;

import com.example.filmorate.dao.FriendshipDao;
import com.example.filmorate.dao.UserDao;
import com.example.filmorate.model.ErrorResponse;
import com.example.filmorate.model.User;

import jakarta.validation.NoProviderFoundException;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.rmi.ServerException;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserDao userDao;
    private final FriendshipDao friendshipDao;

    @Autowired
    public UserController(UserDao userDao, FriendshipDao friendshipDao) {
        this.userDao = userDao;
        this.friendshipDao = friendshipDao;
    }

    @GetMapping("")
    public List<User> findAll() {
        return userDao.findAll();
    }

    @PostMapping(value = "")
    public User create(@Valid @RequestBody User user) {
        Optional<User> newUser = userDao.create(user);
        return newUser.orElseThrow(() -> new NoProviderFoundException("User not found"));
    }

    @PutMapping(value = "")
    @ResponseStatus(HttpStatus.CREATED)
    public User update(@Valid @RequestBody User user) throws ErrorResponse {

        if (user.getId() > 0) {
            return userDao.update(user);
        } else {
            throw new NoProviderFoundException("'id' обязательное поле");
        }
    }

    @GetMapping("/{id}")
    public User findById(@RequestBody @PathVariable Integer id) {
        Optional<User> current = userDao.findById(id);
        return current.orElseThrow(() -> new NoProviderFoundException("User not found"));
    }

    @DeleteMapping("/{id}")
    public String removeUser(@PathVariable Integer id) {
        return userDao.removeUser(id);
    }

    @PostMapping(value = "/{id}/follow/{friendId}")
    public String follow(@PathVariable int id, @PathVariable int friendId) {
        return friendshipDao.follow(id, friendId);
    }

    @GetMapping("/{id}/followers")
    public List<User> findFollowersById(@RequestBody @PathVariable Integer id) {
        return friendshipDao.findUsersByFriendshipState(id, true);
    }

    @GetMapping("/{id}/follows")
    public List<User> findFollowsById(@RequestBody @PathVariable Integer id) {
        return friendshipDao.findUsersByFriendshipState(id, false);
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriendsById(@RequestBody @PathVariable Integer id) {
        return friendshipDao.findFriendsById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public String addFriend(@PathVariable int id, @PathVariable int friendId) {
        return friendshipDao.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public String removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        return friendshipDao.removeFriend(id, friendId);
    }

    @DeleteMapping("/{id}/follows/{friendId}")
    public String unfollow(@PathVariable Integer id, @PathVariable Integer friendId) throws ServerException {
        return friendshipDao.unfollow(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        return friendshipDao.getCrossFriends(id, friendId);
    }
}
