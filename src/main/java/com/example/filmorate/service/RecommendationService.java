package com.example.filmorate.service;

import com.example.filmorate.model.Film;
import com.example.filmorate.storage.FilmDBStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.rmi.ServerException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class RecommendationService {
    private final JdbcTemplate jdbcTemplate;
    private final FeedbackService feedbackService;
    private final FilmDBStorage filmDbStorage;

    @Autowired
    public RecommendationService(JdbcTemplate jdbcTemplate,
                                 FilmDBStorage filmDbStorage,
                                 FeedbackService feedbackService) {
        this.jdbcTemplate = jdbcTemplate;
        this.feedbackService = feedbackService;
        this.filmDbStorage = filmDbStorage;
    }

    public List<Film> getRecommendations(@PathVariable int userId) {
        feedbackService.idChecker("users", userId, " пользователь");
        String sql = "select * from films where id in (select film_id from recommendations where friend_id=?);";
        return jdbcTemplate.query(sql, (rs, _) -> filmDbStorage.makeFilms(rs), userId);
    }

    public int recommendToFriend(int userId, int filmId, int authorId) throws ServerException {
        Timestamp recommendationDate = Timestamp.from(Instant.now());

        feedbackService.idChecker("films", filmId, " фильм");
        feedbackService.idChecker("users", userId, " пользователь");
        feedbackService.idChecker("users", authorId, " пользователь");
        String checkDuplicateSql = "select count(id) from recommendations where film_id=? and author_id=? and friend_id=?";
        int duplicateCounter =
                jdbcTemplate.queryForObject(checkDuplicateSql, new Object[]{filmId, authorId, userId}, Integer.class);

        if (duplicateCounter > 0) {
            return -1;
        }
        String resSql =
                "insert into recommendations (film_id, author_id, friend_id, recommendation_date) values (?, ?, ?, ?);";
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(resSql);
            preparedStatement.setInt(1, filmId);
            preparedStatement.setInt(2, authorId);
            preparedStatement.setInt(3, userId);
            preparedStatement.setTimestamp(4, recommendationDate);
            return preparedStatement;
        });

        if (rowsAffected > 0) {
            String sqlQuery = "SELECT max(id) as id from recommendations;";
            Integer id = jdbcTemplate.queryForObject(sqlQuery, Integer.class);

            if (id != null) {
                return id;
            }
        }
        throw new ServerException("Непредвиденная ошибка сервера.");
    }

    public String recommendToAll(int userId, int filmId) {
        String friendsIdsSql = "select from_user as user_id from friendship where stateid=1 and to_user=? " +
                "union all " +
                "select to_user  as user_id from friendship where stateid=1 and from_user=?;";
        List<Integer> recIds = jdbcTemplate.query(friendsIdsSql, (rs, _) -> {
            int friendId = rs.getInt("user_id");
            try {
                return recommendToFriend(friendId, filmId, userId);
            } catch (ServerException e) {
                throw new RuntimeException(e);
            }
        }, userId, userId).stream().filter(i -> i >= 0).toList();

        if (!recIds.isEmpty()) {
            return STR."Добавлены рекомендации со следующими id: \{recIds}";
        } else {
            return "Вы уже рекомендовали это фильм все своим друзьям";
        }
    }
    public String deleteRecommendations(int authorId, int recId) throws ServerException {
        feedbackService.idChecker("users", authorId, " пользователь");
        feedbackService.idChecker("recommendations", recId, "а рекомендация");
        String checkOwnerSql = "select count(*) from recommendations where author_id = ? and id = ?;";
        Integer recCounter = jdbcTemplate.queryForObject(checkOwnerSql, Integer.class, authorId, recId);

        if (recCounter > 0) {
            String sql = "delete from recommendations where author_id = ? and id = ?;";
            int rowsAffected = jdbcTemplate.update(sql, authorId, recId);
            if (rowsAffected > 0) {
                return STR."Рекомендация с id=\{recId} удалена.";
            }
        }
        throw new ServerException("Непредвиденная ошибка сервера.");
    }
}
