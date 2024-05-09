package com.example.filmorate.storage;

import com.example.filmorate.model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    @Override
    public HashMap<Integer, User> findAll() {
        return users;
    }

    @Override
    public List<Object> create(User user) {
        List<Object> resultsList = new ArrayList<>();

        int id;
        if (!users.isEmpty()) {
            id = users.size() * 13;
        } else {
            id = 1;
        }
        user.setId(id);

        if (user.getEmail() == null || isValidEmail(user.getEmail())) {
            String errorMessage = "Email обязателен к заполнению и должен соответствовать стандартам.";
            resultsList.add(errorMessage);
        }

        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            String errorMessage = "Логин обязателен и не может содержать пробелы.";
            resultsList.add(errorMessage);
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(Instant.now())) {
            String errorMessage = "Пришельцам из будущего доступ запрещен.";
            resultsList.add(errorMessage);
        }

        if (resultsList.isEmpty()) {
            users.put(id, user);
            resultsList.add(user);
        }
        return resultsList;
    }

    @Override
    public List<Object> update(User user) {
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
                List<Object> result = new ArrayList<>();
                result.add(currentUser);
                return result;
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
}