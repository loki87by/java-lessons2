package com.example.catsgram.service;

import com.example.catsgram.dao.UserDao;
import com.example.catsgram.model.User;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService (UserDao userDao) {
        this.userDao=userDao;
    }

    public Optional<User> findUserById(String id) {
        return userDao.findUserById(id);
    }

    public Optional<User> create(User user) {
        /*String login = user.getId();
        String nick = user.getNickname();
        String name = user.getUsername();*/
        /*users.put(login, user);
        return user;*/
        return userDao.create(user);
    }
    /*private final HashMap<String, User> users = new HashMap<>();

    public HashMap<String, User> findAll() {
        return users;
    }

    public User create(User user) {
        String email = user.getEmail();
        users.put(email, user);
        return user;
    }

    public User update(User user) {
        String email = user.getEmail();
        User currentUser = users.get(email);

        if (currentUser == null) {
            return create(user);
        } else {

            if (user.getBirthdate() != null) {
                currentUser.setBirthdate(user.getBirthdate());
            }

            if (user.getNickname() != null && !user.getNickname().isEmpty()) {
                currentUser.setNickname(user.getNickname());
            }
            return user;
        }
    }*/
}
