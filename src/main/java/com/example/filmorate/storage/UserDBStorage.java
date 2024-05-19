package com.example.filmorate.storage;

import com.example.filmorate.model.User;

import jakarta.validation.ValidationException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
    public User makeUsers(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getString("birthday"),
                rs.getInt("id")
        );
    }

    private void checkUserBirthday(String birthday) {

        if (birthday != null) {
            String errorMessage;
            try {
                LocalDate date = LocalDate.parse(birthday);

                if (date.atStartOfDay(ZoneId.systemDefault()).toInstant().isAfter(Instant.now())) {
                    errorMessage = "Пришельцам из будущего доступ запрещен.";
                    throw new ValidationException(errorMessage);
                }
            } catch (DateTimeParseException e) {
                errorMessage = e.getMessage();
                throw new ValidationException(errorMessage);
            }
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUsers(rs));
    }

    @Override
    public Optional<User> create(User user) {
        String errorMessage;

        if (user.getEmail() == null || isInvalidEmail(user.getEmail())) {
            errorMessage = "Email обязателен к заполнению и должен соответствовать стандартам.";
            throw new ValidationException(errorMessage);
        }

        if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            errorMessage = "Логин обязателен, не может быть пустым или содержать пробелы.";
            throw new ValidationException(errorMessage);
        }

        checkUserBirthday(user.getBirthday());

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
            String sqlQuery = "SELECT max(id) as id from users;";
            Integer id = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
            if (id != null) {
                user.setId(id);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public User update(User user) {
        int id = user.getId();
        String errorMessage;

        if (user.getEmail() != null && isInvalidEmail(user.getEmail())) {
            errorMessage = "Email должен соответствовать стандартам.";
            throw new ValidationException(errorMessage);
        }

        if (user.getLogin() != null && (user.getLogin().contains(" ") || user.getLogin().isEmpty())) {
            errorMessage = "Логин, не может быть пустым или содержать пробелы.";
            throw new ValidationException(errorMessage);
        }
        checkUserBirthday(user.getBirthday());

        HashMap<String, String> userParams = new HashMap<>();

        userParams.put("email", user.getEmail());
        userParams.put("login", user.getLogin());
        userParams.put("name", user.getName());
        userParams.put("birthday", user.getBirthday());
        String sqlStart = "UPDATE users SET ";
        List<String> notNullParamsList = new ArrayList<>();
        List<Object> paramValues = new ArrayList<>();

        for (String key : userParams.keySet()) {
            if (userParams.get(key) != null) {
                notNullParamsList.add(key + " = ?");
                paramValues.add(userParams.get(key));
            }
        }
        String sql = sqlStart + String.join(", ", notNullParamsList) + " WHERE id = ?";
        paramValues.add(id);
        int rowsAffected = jdbcTemplate.update(sql, paramValues.toArray());

        if (rowsAffected > 0) {
            String sqlQuery = "SELECT * from users where id = ?;";
            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUsers(rs), id).getFirst();
        }
        return null;
    }

    @Override
    public boolean isInvalidEmail(String emailValue) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(emailValue);
        return !matcher.matches();
    }
}
