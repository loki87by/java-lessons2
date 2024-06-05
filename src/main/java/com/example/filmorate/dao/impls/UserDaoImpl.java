package com.example.filmorate.dao.impls;

import com.example.filmorate.dao.FeedDao;
import com.example.filmorate.dao.UserDao;
import com.example.filmorate.model.User;
import com.example.filmorate.service.FilmService;
import com.example.filmorate.storage.FilmDBStorage;
import com.example.filmorate.storage.UserDBStorage;

import jakarta.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserDBStorage userDBStorage;
    private final FeedDao feedDao;
    private final FilmService filmService;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate, UserDBStorage userDBStorage, FeedDao feedDao, FilmService filmService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDBStorage = userDBStorage;
        this.feedDao = feedDao;
        this.filmService = filmService;
    }
    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users;";
        return jdbcTemplate.query(sql, (rs, _) -> userDBStorage.makeUsers(rs));
    }

    @Override
    public Optional<User> findById (Integer id) {
        String sql = "SELECT * FROM users where id = ?;";
        User user = jdbcTemplate.query(sql, (rs, _) -> userDBStorage.makeUsers(rs), id).getFirst();

        if (user != null) {
            return Optional.of(user);
        }
        throw new NoSuchElementException(STR."Пользователь с id=\{id} не найден.");
    }

    @Override
    public Optional<User> create(User user) {
        String errorMessage;

        if (user.getEmail() == null || userDBStorage.isInvalidEmail(user.getEmail())) {
            errorMessage = "Email обязателен к заполнению и должен соответствовать стандартам.";
            throw new ValidationException(errorMessage);
        }

        if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            errorMessage = "Логин обязателен, не может быть пустым или содержать пробелы.";
            throw new ValidationException(errorMessage);
        }
        userDBStorage.checkUserBirthday(user.getBirthday());
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
                feedDao.addToFeed(2, id);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public User update(User user) {
        int id = user.getId();
        String errorMessage;

        if (user.getEmail() != null && userDBStorage.isInvalidEmail(user.getEmail())) {
            errorMessage = "Email должен соответствовать стандартам.";
            throw new ValidationException(errorMessage);
        }

        if (user.getLogin() != null && (user.getLogin().contains(" ") || user.getLogin().isEmpty())) {
            errorMessage = "Логин, не может быть пустым или содержать пробелы.";
            throw new ValidationException(errorMessage);
        }
        userDBStorage.checkUserBirthday(user.getBirthday());
        HashMap<String, String> userParams = new HashMap<>();
        userParams.put("email", user.getEmail());
        userParams.put("login", user.getLogin());
        userParams.put("name", user.getName());
        userParams.put("birthday", user.getBirthday());
        String sqlStart = "UPDATE users SET ";
        int rowsAffected = FilmDBStorage.getSqlWithParams(id, userParams, sqlStart, jdbcTemplate, "users");

        if (rowsAffected > 0) {
            String sqlQuery = "SELECT * from users where id = ?;";
            return jdbcTemplate.query(sqlQuery, (rs, _) -> userDBStorage.makeUsers(rs), id).getFirst();
        }
        return null;
    }
    @Override
    public String removeUser(int id) {
        filmService.checkUser(id);
        String sql = "delete from users where user_id = ?;";
        jdbcTemplate.update(sql, id);
        feedDao.addToFeed(4, id);
        return STR."Пользователь с id=\{id} удалил свой профиль.";
    }
}
