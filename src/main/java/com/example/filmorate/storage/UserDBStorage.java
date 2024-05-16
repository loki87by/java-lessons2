package com.example.filmorate.storage;

import com.example.filmorate.model.User;
import jakarta.validation.ValidationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserDBStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public HashMap<Integer, User> findAll() {
        return null;
    }

    @Override
    public Optional<User> create(User user) {
        String errorMessage;
        if (user.getEmail() == null || isValidEmail(user.getEmail())) {
            errorMessage = "Email обязателен к заполнению и должен соответствовать стандартам.";
            throw new ValidationException(errorMessage);
        }

        if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            errorMessage = "Логин обязателен, не может быть пустым или содержать пробелы.";
            throw new ValidationException(errorMessage);
        }

        if (user.getBirthday() != null) {
            try {
                LocalDate date = LocalDate.parse(user.getBirthday());

                if (date.atStartOfDay(ZoneId.systemDefault()).toInstant().isAfter(Instant.now())) {
                    errorMessage = "Пришельцам из будущего доступ запрещен.";
                    throw new ValidationException(errorMessage);
                }
            } catch (DateTimeParseException e) {
                errorMessage = e.getMessage();
                throw new ValidationException(errorMessage);
            }
        }

        String sql = "INSERT INTO users (email, login, birthday";

        if (user.getName() != null) {
            sql += ", name) VALUES (?, ?, ?, ?)";
        } else {
            sql += ") VALUES (?, ?, ?)";
        }
        String finalSql = sql;
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(finalSql);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getBirthday());

            if (user.getName() != null) {
                preparedStatement.setString(4, user.getName());
            }
            return preparedStatement;
        });

        if (rowsAffected > 0) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public List<Object> update(User user) {
        return null;
    }

    @Override
    public boolean isValidEmail(String emailValue) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(emailValue);
        return !matcher.matches();
    }
}
