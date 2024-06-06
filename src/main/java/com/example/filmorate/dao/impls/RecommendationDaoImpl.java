package com.example.filmorate.dao.impls;

import com.example.filmorate.dao.FeedDao;
import com.example.filmorate.dao.RecommendationDao;
import com.example.filmorate.model.Film;
import com.example.filmorate.service.FeedbackService;
import com.example.filmorate.storage.FilmDBStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.rmi.ServerException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
public class RecommendationDaoImpl implements RecommendationDao {

    private final JdbcTemplate jdbcTemplate;
    private final FeedbackService feedbackService;
    private final FilmDBStorage filmDbStorage;
    private final FeedDao feedDao;

    @Autowired
    public RecommendationDaoImpl(JdbcTemplate jdbcTemplate,
                                 FilmDBStorage filmDbStorage,
                                 FeedDao feedDao,
                                 FeedbackService feedbackService) {
        this.jdbcTemplate = jdbcTemplate;
        this.feedbackService = feedbackService;
        this.feedDao = feedDao;
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        feedbackService.idChecker("users", userId, " пользователь");
        String sql = "select * from films where id in (select film_id from recommendations where friend_id=?);";
        return jdbcTemplate.query(sql, (rs, _) -> filmDbStorage.makeFilms(rs), userId);
    }

    @Override
    public int recommendToFriend(int userId, int filmId, int authorId) throws ServerException {
        Timestamp recommendationDate = Timestamp.from(Instant.now());

        feedbackService.idChecker("films", filmId, " фильм");
        feedbackService.idChecker("users", userId, " пользователь");
        feedbackService.idChecker("users", authorId, " пользователь");
        String checkDuplicateSql = "select count(id) from recommendations where film_id=? and author_id=? and friend_id=?";
        int duplicateCounter =
                Objects.requireNonNull(jdbcTemplate.queryForObject(checkDuplicateSql, Integer.class, filmId, authorId, userId));

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
            feedDao.addToFeed(20, authorId, filmId, String.valueOf(userId));

            if (id != null) {
                return id;
            }
        }
        throw new ServerException("Непредвиденная ошибка сервера.");
    }

    @Override
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

    @Override
    public String deleteRecommendations(int authorId, int recId) throws ServerException {
        feedbackService.idChecker("users", authorId, " пользователь");
        feedbackService.idChecker("recommendations", recId, "а рекомендация");
        String checkOwnerSql = "select count(*) from recommendations where author_id = ? and id = ?;";
        Integer recCounter = jdbcTemplate.queryForObject(checkOwnerSql, Integer.class, authorId, recId);

        if (recCounter != null && recCounter > 0) {
            String getFilmIdSql = "select film_id from recommendations where author_id = ? and id = ?;";
            Integer filmId = Objects.requireNonNull(jdbcTemplate.queryForObject(getFilmIdSql, Integer.class, authorId, recId));
            feedDao.addToFeed(25, authorId, filmId);
            String sql = "delete from recommendations where author_id = ? and id = ?;";
            int rowsAffected = jdbcTemplate.update(sql, authorId, recId);
            if (rowsAffected > 0) {
                return STR."Рекомендация с id=\{recId} удалена.";
            }
        }
        throw new ServerException("Непредвиденная ошибка сервера.");
    }
}
