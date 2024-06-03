package com.example.filmorate.service;

import com.example.filmorate.model.User;
import com.example.filmorate.storage.UserStorage;

import jakarta.validation.NoProviderFoundException;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(@Qualifier("userDBStorage") UserStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    private String getSqlByRelationship(int stateId, boolean isFromUser) {
        String column = isFromUser ? "from_user" : "to_user";
        return STR."SELECT \{
                column} AS id FROM friendship WHERE \{
                isFromUser ? "to_user = ?" : "from_user = ?"} AND stateId = \{
                stateId}";
    }

    public List<User> findUsersByFriendshipState(int userId, boolean isFromUser) {
        String sql = STR."SELECT * FROM users WHERE id IN (\{getSqlByRelationship(0, isFromUser)})";
        return jdbcTemplate.query(sql, (rs, _) -> userStorage.makeUsers(rs), userId);
    }

    public List<User> findFriendsById(int id) {
        String sql =
                STR."SELECT * FROM users where id in (\{
                        getSqlByRelationship(1, true)} union \{
                        getSqlByRelationship(1, false)})";
        return jdbcTemplate.query(sql, (rs, _) -> userStorage.makeUsers(rs), id, id);
    }

    private boolean[] compareFriendsState(int firstId, int secondId) {
        boolean[] results = new boolean[2];
        String sql = "select count(*) from friendship where from_user = ? and to_user = ?";
        int i1 = jdbcTemplate.queryForObject(sql, Integer.class, firstId, secondId);
        results[0] = i1 > 0;
        int i2 = jdbcTemplate.queryForObject(sql, Integer.class, secondId, firstId);
        results[1] = i2 > 0;
        return results;
    }

    public String follow(int firstId, int secondId) {
        String sql = "INSERT INTO friendship (from_user, to_user, stateId) VALUES (?, ?, 0)";
        boolean[] compares = compareFriendsState(firstId, secondId);

        if (compares[0] || compares[1]) {
            throw new ValidationException(
                    "Вы пытаетесь выполнить излишнюю операцию, проверьте друзей, подписки и подписчиков.");
        }
        try {
            int rowsAffected = jdbcTemplate.update(sql, firstId, secondId);

            if (rowsAffected > 0) {
                return STR."Теперь пользователь `id=\{firstId}' подписан на пользователя `id=\{secondId}'.";
            } else {
                return "Что-то пошло не так.";
            }
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().toLowerCase().contains("key(to_user)")) {
                //System.out.println("to error user = " + secondId);
                throw new NoProviderFoundException(STR."Пользователь с `id=\{secondId}` не найден.");
            } else if (e.getMessage().toLowerCase().contains("key(from_user)")) {
                //System.out.println(STR."from error user = \{firstId}");
                throw new NoProviderFoundException(STR."Пользователь с `id=\{firstId}` не найден.");
            } else {
                throw new NoProviderFoundException(e.getMessage().replace("; ", "\n"));
            }
        }
    }

    public String addFriend(int userId, int followerId) {
        String sql = "select count(*) from friendship where from_user = ? and to_user = ? AND stateId = 0";
        boolean follower = jdbcTemplate.queryForObject(sql, Integer.class, followerId, userId) == 1;
        if (follower) {
            String sqlQuery = "UPDATE friendship SET stateId=1 where from_user = ? and to_user = ?";
            int rowsAffected = jdbcTemplate.update(sqlQuery, followerId, userId);

            if (rowsAffected > 0) {
                return STR."Пользователь `id=\{userId}' принял запрос дружбы от пользователя `id=\{followerId}'.";
            } else {
                return "Что-то пошло не так.";
            }
        } else {
            return STR."Нельзя добавить в друзья пользователя `id=\{followerId}', пока он не подписан на вас.";
        }
    }

    public String removeFriend(int userId, int removedId) {
        boolean fromUser = compareFriendsState(userId, removedId)[0];
        boolean toUser = compareFriendsState(userId, removedId)[1];

        if (!fromUser && toUser) {
            String sqlQuery = "UPDATE friendship SET stateId=0 where from_user = ? and to_user = ?";
            int rowsAffected = jdbcTemplate.update(sqlQuery, removedId, userId);
            String sql = "select count(*) from friendship where from_user = ? and to_user = ? AND stateId = 0";
            boolean follower = jdbcTemplate.queryForObject(sql, Integer.class, removedId, userId) == 1;

            if (rowsAffected > 0 && !follower) {
                return STR."Пользователь `id=\{userId}' разорвал дружбу с пользователем `id=\{removedId}'.";
            }
        } else if (fromUser && !toUser) {
            String sql = "select count(*) from friendship where from_user = ? and to_user = ? AND stateId = 1";
            boolean isFriendship = (jdbcTemplate.queryForObject(sql, Integer.class, removedId, userId) +
                    (jdbcTemplate.queryForObject(sql, Integer.class, userId, removedId))) > 0;

            if (isFriendship) {
                String sqlQuery = "Delete from friendship where from_user = ? and to_user = ?";
                int rowsAffected = jdbcTemplate.update(sqlQuery, userId, removedId);

                if (rowsAffected > 0) {
                    return STR."Дружба разорвана. \{follow(removedId, userId)}";
                }
            }
        }
        throw new NoProviderFoundException("Нельзя так просто взять и удалить вымышленного друга.");
    }

    public List<User> getCrossFriends(int firstId, int secondId) {
        String sql =
                STR."SELECT * FROM users where id in (SELECT id from (SELECT id FROM (SELECT * FROM (\{
                getSqlByRelationship(1, true)} union \{
                getSqlByRelationship(1, false)})) AS user1 union all SELECT id FROM (SELECT * FROM (\{
                getSqlByRelationship(1, true)} union \{
                getSqlByRelationship(1, false)})) AS user2 ) AS all_ids GROUP BY id HAVING COUNT(id) > 1);"
                ;
        return jdbcTemplate.query(sql, (rs, rowNum) -> userStorage.makeUsers(rs), firstId, firstId, secondId, secondId);
    }
}
