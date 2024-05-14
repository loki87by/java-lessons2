package com.example.filmorate.service;

import com.example.filmorate.model.User;
import com.example.filmorate.storage.UserStorage;

import jakarta.validation.NoProviderFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public String addFriend(int firstId, int secondId) {
        User firstUser = userStorage.findAll().get(firstId);
        User secondUser = userStorage.findAll().get(secondId);

        if (firstUser != null && secondUser != null) {
            firstUser.getFriends().add(secondId);
            secondUser.getFriends().add(firstId);
            return "Пользователи с id=" + firstId + " и id=" + secondId + " стали друзьями.";
        } else if (firstUser == null) {
            String errorMessage = "Пользователь с id=" + firstId + " не найден.";
            throw new NoProviderFoundException(errorMessage);
        } else {
            String errorMessage = "Пользователь с id=" + secondId + " не найден.";
            throw new NoProviderFoundException(errorMessage);
        }
    }

    public String removeFriend(int firstId, int secondId) {
        User firstUser = userStorage.findAll().get(firstId);
        User secondUser = userStorage.findAll().get(secondId);

        if (firstUser != null && secondUser != null) {
            firstUser.getFriends().remove(secondId);
            secondUser.getFriends().remove(firstId);
            return "Пользователи с id=" + firstId + " и id=" + secondId + " перестали дружить.";
        } else if (firstUser == null) {
            String errorMessage = "Пользователь с id=" + firstId + " не найден.";
            throw new NoProviderFoundException(errorMessage);
        } else {
            String errorMessage = "Пользователь с id=" + secondId + " не найден.";
            throw new NoProviderFoundException(errorMessage);
        }
    }

    public List<Integer> getCrossFriends(int firstId, int secondId) {
        User firstUser = userStorage.findAll().get(firstId);
        User secondUser = userStorage.findAll().get(secondId);
        List<Integer> crossFriends = new ArrayList<>();

        if (firstUser != null && secondUser != null) {
            Set<Integer> firstFriendList = firstUser.getFriends();
            Set<Integer> secondFriendList = secondUser.getFriends();
            for (int id : firstFriendList) {
                for (int friendId : secondFriendList) {

                    if (id == friendId) {
                        crossFriends.add(id);
                    }
                }
            }
        }
        return crossFriends;
    }
}
