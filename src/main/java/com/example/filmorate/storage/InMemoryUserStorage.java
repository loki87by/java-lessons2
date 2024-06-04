package com.example.filmorate.storage;

import com.example.filmorate.model.User;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final List<User> users = new ArrayList<>();

    @Override
    public boolean isInvalidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public Optional<User> create(User user) {
        List<Object> resultsList = new ArrayList<>();

        if (user.getEmail() == null || isInvalidEmail(user.getEmail())) {
            String errorMessage = "Email обязателен к заполнению и должен соответствовать стандартам.";
            resultsList.add(errorMessage);
        }

        if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            String errorMessage = "Логин обязателен, не может быть пустым или содержать пробелы.";
            resultsList.add(errorMessage);
        }

        if (user.getBirthday() != null && LocalDate.parse(user.getBirthday())
                .atStartOfDay(ZoneId.systemDefault()).toInstant().isAfter(Instant.now())) {
            String errorMessage = "Пришельцам из будущего доступ запрещен.";
            resultsList.add(errorMessage);
        }

        if (resultsList.isEmpty()) {
            users.add(user);
            resultsList.add(user);
        }
        return Optional.empty();
    }

    @Override
    public User update(User user) {
            int id = 0;
        return users.get(id);
    }
}