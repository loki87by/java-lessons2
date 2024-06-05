package com.example.filmorate.dao.impls;

import com.example.filmorate.dao.FeedDao;
import com.example.filmorate.dao.FriendshipDao;
import com.example.filmorate.model.User;
import com.example.filmorate.service.UserService;
import com.example.filmorate.storage.UserDBStorage;

import jakarta.validation.NoProviderFoundException;
import jakarta.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.rmi.ServerException;
import java.util.List;
import java.util.Objects;

@Component
public class FriendshipDaoImpl implements FriendshipDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserDBStorage userDBStorage;
    private final UserService userService;
    private final FeedDao feedDao;

    @Autowired
    public FriendshipDaoImpl(JdbcTemplate jdbcTemplate,
                             UserService userService,
                             FeedDao feedDao,
                             UserDBStorage userDBStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
        this.userDBStorage = userDBStorage;
        this.feedDao = feedDao;
    }

    private String getSqlBR(int sid, boolean ifu) {
        return userService.getSqlByRelationship(sid, ifu);
    }

    @Override
    public List<User> findUsersByFriendshipState(int userId, boolean isFromUser) {
        String sql = STR."SELECT * FROM users WHERE id IN (\{getSqlBR(0, isFromUser)})";
        return jdbcTemplate.query(sql, (rs, _) -> userDBStorage.makeUsers(rs), userId);
    }

    @Override
    public List<User> findFriendsById(int id) {
        String sql =
                STR."SELECT * FROM users where id in (\{
                        getSqlBR(1, true)} union \{
                        getSqlBR(1, false)})";
        return jdbcTemplate.query(sql, (rs, _) -> userDBStorage.makeUsers(rs), id, id);
    }

    @Override
    public String follow(int firstId, int secondId) {
        String sql = "INSERT INTO friendship (from_user, to_user, stateId) VALUES (?, ?, 0)";
        boolean[] compares = userService.compareFriendsState(firstId, secondId);

        if (compares[0] || compares[1]) {
            throw new ValidationException(
                    "Вы пытаетесь выполнить излишнюю операцию, проверьте друзей, подписки и подписчиков.");
        }
        try {
            int rowsAffected = jdbcTemplate.update(sql, firstId, secondId);

            if (rowsAffected > 0) {
                feedDao.addToFeed(5, firstId, secondId);
                return STR."Теперь пользователь `id=\{firstId}' подписан на пользователя `id=\{secondId}'.";
            } else {
                return "Что-то пошло не так.";
            }
        } catch (DataIntegrityViolationException e) {

            if (e.getMessage().toLowerCase().contains("key(to_user)")) {
                throw new NoProviderFoundException(STR."Пользователь с `id=\{secondId}` не найден.");
            } else if (e.getMessage().toLowerCase().contains("key(from_user)")) {
                throw new NoProviderFoundException(STR."Пользователь с `id=\{firstId}` не найден.");
            } else {
                throw new NoProviderFoundException(e.getMessage().replace("; ", "\n"));
            }
        }
    }

    @Override
    public String addFriend(int userId, int followerId) {
        String sql = "select count(*) from friendship where from_user = ? and to_user = ? AND stateId = 0";
        boolean follower =
                Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Integer.class, followerId, userId)) == 1;

        if (follower) {
            String sqlQuery = "UPDATE friendship SET stateId=1 where from_user = ? and to_user = ?";
            int rowsAffected = jdbcTemplate.update(sqlQuery, followerId, userId);

            if (rowsAffected > 0) {
                feedDao.addToFeed(6, userId, followerId);
                return STR."Пользователь `id=\{userId}' принял запрос дружбы от пользователя `id=\{followerId}'.";
            } else {
                return "Что-то пошло не так.";
            }
        } else {
            return STR."Нельзя добавить в друзья пользователя `id=\{followerId}', пока он не подписан на вас.";
        }
    }

    @Override
    public String removeFriend(int userId, int removedId) {
        boolean fromUser = userService.compareFriendsState(userId, removedId)[0];
        boolean toUser = userService.compareFriendsState(userId, removedId)[1];

        if (!fromUser && toUser) {
            String sqlQuery = "UPDATE friendship SET stateId=0 where from_user = ? and to_user = ?";
            int rowsAffected = jdbcTemplate.update(sqlQuery, removedId, userId);
            String sql = "select count(*) from friendship where from_user = ? and to_user = ? AND stateId = 0";
            boolean follower =
                    Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Integer.class, removedId, userId)) == 1;

            if (rowsAffected > 0 && !follower) {
                feedDao.addToFeed(8, userId, removedId);
                return STR."Пользователь `id=\{userId}' разорвал дружбу с пользователем `id=\{removedId}'.";
            }
        } else if (fromUser && !toUser) {
            String sql = "select count(*) from friendship where from_user = ? and to_user = ? AND stateId = 1";
            boolean isFriendship =
                    (Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Integer.class, removedId, userId)) +
                            Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Integer.class, userId, removedId))) > 0;

            if (isFriendship) {
                String sqlQuery = "Delete from friendship where from_user = ? and to_user = ?";
                int rowsAffected = jdbcTemplate.update(sqlQuery, userId, removedId);

                if (rowsAffected > 0) {
                    feedDao.addToFeed(8, userId, removedId);
                    return STR."Дружба разорвана. \{follow(removedId, userId)}";
                }
            }
        }
        throw new NoProviderFoundException("Нельзя так просто взять и удалить вымышленного друга.");
    }

    @Override
    public List<User> getCrossFriends(int firstId, int secondId) {
        String sql =
                STR."SELECT * FROM users where id in (SELECT id from (SELECT id FROM (SELECT * FROM (\{
                        getSqlBR(1, true)} union \{
                        getSqlBR(1, false)})) AS user1 union all SELECT id FROM (SELECT * FROM (\{
                        getSqlBR(1, true)} union \{
                        getSqlBR(1, false)})) AS user2 ) AS all_ids GROUP BY id HAVING COUNT(id) > 1);";
        return jdbcTemplate.query(sql, (rs, _) -> userDBStorage.makeUsers(rs), firstId, firstId, secondId, secondId);
    }

    @Override
    public String unfollow(int id, int friendId) throws ServerException {
        String sql = "select id from friendship where from_user = ? and to_user = ?";
        Integer recId = jdbcTemplate.queryForObject(sql, Integer.class, id, friendId);

        if (recId != null && recId > 0) {
            String stateSql = "select stateId from friendship where id = ?";
            Integer stateId = Objects.requireNonNull(jdbcTemplate.queryForObject(stateSql, Integer.class, recId));

            if (stateId == 1) {
                removeFriend(id, friendId);
            } else {
                String newSql = "delete from friendship where id = ?";
                int rowsAffected = jdbcTemplate.update(newSql, recId);

                if (rowsAffected > 0) {
                    feedDao.addToFeed(7, id, friendId);
                    return STR."Пользователь `id=\{id}' отписался от пользователя `id=\{friendId}'.";
                }
            }
        }
        throw new ServerException("Что-то пошло не так, повторите запрос позже.");
    }
}
