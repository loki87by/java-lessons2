package com.example.filmorate.service;
import com.example.filmorate.model.User;
import com.example.filmorate.storage.UserStorage;
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

    public void addFriend(int firstId, int secondId) {
        User firstUser = userStorage.findAll().get(firstId);
        User secondUser = userStorage.findAll().get(secondId);

        if (firstUser != null && secondUser != null) {
            firstUser.getFriends().add(secondId);
            secondUser.getFriends().add(firstId);
        }
    }

    public void removeFriend(int firstId, int secondId) {
        User firstUser = userStorage.findAll().get(firstId);
        User secondUser = userStorage.findAll().get(secondId);

        if (firstUser != null && secondUser != null) {
            firstUser.getFriends().remove(secondId);
            secondUser.getFriends().remove(firstId);
        }
    }

    public List<Integer> getCrossFriends(int firstId, int secondId) {
        User firstUser = userStorage.findAll().get(firstId);
        User secondUser = userStorage.findAll().get(secondId);
        List<Integer> crossFriends = new ArrayList<>();

        if (firstUser != null && secondUser != null) {
            Set<Integer> firstFriendList = firstUser.getFriends();
            Set<Integer> secondFriendList = secondUser.getFriends();
            for (int id:firstFriendList) {
                for (int friendId:secondFriendList) {

                    if(id == friendId) {
                        crossFriends.add(id);
                    }
                }
            }
        }
        return crossFriends;
    }
}
