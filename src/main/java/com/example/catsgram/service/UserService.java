package com.example.catsgram.service;

import com.example.catsgram.exceptions.InvalidEmailException;
import com.example.catsgram.exceptions.UserAlreadyExistException;
import com.example.catsgram.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserService {
    private final HashMap<String, User> users = new HashMap<>();

    public HashMap<String, User> findAll() {
        return users;
    }

    public User create(User user) throws InvalidEmailException, UserAlreadyExistException {
        String email = user.getEmail();

        if (email.isEmpty()) {
            throw new InvalidEmailException("email не может быть пустым");
        }

        if (users.containsKey(email)) {
            throw new UserAlreadyExistException("Пользователь с таким email уже существует");
        } else {
            users.put(email, user);
            return user;
        }
    }

    public User update(User user) throws UserAlreadyExistException, InvalidEmailException {
        String email = user.getEmail();

        if (email.isEmpty()) {
            throw new InvalidEmailException("email не может быть пустым");
        }
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
    }
}
