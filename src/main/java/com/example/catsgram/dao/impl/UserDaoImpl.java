package com.example.catsgram.dao.impl;

import com.example.catsgram.dao.UserDao;
import com.example.catsgram.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Optional<User> findUserById(String id) {
        System.out.println("ok");
        String sql = "SELECT * FROM cat_user WHERE id = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        if (userRows.next()) {
            User user = new User(
                    userRows.getString("id"),
                    userRows.getString("username"),
                    userRows.getString("nickname")
            );
            log.info("Найден пользователь: {} {}", userRows.getString("id"), userRows.getString("nickname"));
            return Optional.of(user);
        }
        log.info("пользователь c id {} не Найден.", userRows.getString("id"));
        return Optional.empty();
    }

    @Override
    public Optional<User> create(User user) {
        String sql = "INSERT INTO public.cat_user (id, nickname, username) VALUES (?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getId());
            preparedStatement.setString(2, user.getNickname());
            preparedStatement.setString(3, user.getUsername());
            return preparedStatement;
        });
        return Optional.empty();
    }
}
