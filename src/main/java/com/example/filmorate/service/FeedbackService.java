package com.example.filmorate.service;

import com.example.filmorate.dao.FeedDao;
import com.example.filmorate.model.Feedback;
import com.example.filmorate.storage.CommentStorage;
import com.example.filmorate.storage.FilmDBStorage;

import jakarta.validation.NoProviderFoundException;
import jakarta.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

import java.rmi.ServerException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class FeedbackService {
    private final JdbcTemplate jdbcTemplate;
    private final CommentStorage commentStorage;
    private final FeedDao feedDao;

    @Autowired
    public FeedbackService(JdbcTemplate jdbcTemplate,
                           CommentStorage commentStorage,
                           FeedDao feedDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.commentStorage = commentStorage;
        this.feedDao = feedDao;
    }

    public List<Feedback> getAllComments() {
        String sqlQuery =
                "SELECT * FROM feedbacks WHERE id > 0;";
        return jdbcTemplate.query(sqlQuery, (rs, _) -> commentStorage.makeFeedbacks(rs));
    }

    public List<Feedback> getComments(int filmId) {
        String sqlQuery =
                "SELECT * FROM feedbacks WHERE film_id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, _) -> commentStorage.makeFeedbacks(rs), filmId);
    }

    public Optional<Feedback> setComment(int filmId, int userId, String content, int rate) {
        Timestamp feedbackDate = Timestamp.from(Instant.now());
        String sql = "INSERT INTO feedbacks (content, feedback_date, film_id, author_id, rate) VALUES (?, ?, ?, ?, ?)";

        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, content);
            preparedStatement.setTimestamp(2, feedbackDate);
            preparedStatement.setInt(3, filmId);
            preparedStatement.setInt(4, userId);
            preparedStatement.setInt(5, rate);
            return preparedStatement;
        });

        if (rowsAffected > 0) {
            feedDao.addToFeed(9, userId, filmId);
            String sqlQuery = "SELECT max(id) as id from feedbacks;";
            Integer id = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
            if (id != null) {
                Feedback feedback = new Feedback(id, content, rate, filmId, userId, feedbackDate);
                return Optional.of(feedback);
            }
        }
        return Optional.empty();
    }

    public void idChecker(String table, int id, String entity) {
        String checkIdSql = STR."select count(*) from \{table} where id = ?";
        Integer idCounter = Objects.requireNonNull(jdbcTemplate.queryForObject(checkIdSql, Integer.class, id));

        if (idCounter != 1) {
            String errorMessage = STR."Не найден\{entity} с id=\{id}";
            throw new NoProviderFoundException(errorMessage);
        }
    }

    public Optional<Feedback> changeComment(Feedback feedback) {
        int id = feedback.getId();
        String errorMessage;
        Timestamp feedbackDate = Timestamp.from(Instant.now());

        idChecker("feedbacks", id, " отзыв");
        idChecker("films", feedback.getFilmId(), " фильм");
        idChecker("users", feedback.getAuthor(), " пользователь");

        if (feedback.getRate() == 0 && feedback.getContent().isEmpty()) {
            errorMessage = "Отзыв должен содержить комментарий и/или оценку.";
            throw new MissingEnvironmentVariableException(errorMessage);
        }

        if (feedback.getRate() < 0 || feedback.getRate() > 10) {
            errorMessage = "Оценка может быть от 1 до 10 или 0 если без оценки.";
            throw new ValidationException(errorMessage);
        }

        HashMap<String, String> feedbackParams = new HashMap<>();

        if (!feedback.getContent().isEmpty()) {
            feedbackParams.put("content", feedback.getContent());
        }

        if(feedback.getFilmId() != 0) {
            feedbackParams.put("film_id", String.valueOf(feedback.getFilmId()));
        }

        if(feedback.getAuthor() != 0) {
            feedbackParams.put("author_id", String.valueOf(feedback.getAuthor()));
        }
        feedbackParams.put("rate", String.valueOf(feedback.getRate()));

        String sqlStart = "UPDATE feedbacks SET ";

        int rowsAffected = FilmDBStorage.getSqlWithParams(id, feedbackParams, sqlStart, jdbcTemplate, "feedbacks");

        if (rowsAffected > 0) {
            String updTimestampSql = "UPDATE feedbacks SET feedback_date = ? where id = ?";
            int rowsChanged = jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(updTimestampSql);
                preparedStatement.setTimestamp(1, feedbackDate);
                preparedStatement.setInt(2, id);
                return preparedStatement;
            });

            if (rowsChanged > 0) {
                String sqlQuery = "SELECT * from feedbacks where id = ?;";
                return Optional.ofNullable(jdbcTemplate.query(sqlQuery, (rs, _) -> commentStorage.makeFeedbacks(rs), id).getFirst());
            }
        }
        return Optional.empty();
    }

    public Optional<Feedback> changeComment(int filmId, int userId, String content, int rate, int cur) {
        Timestamp feedbackDate = Timestamp.from(Instant.now());
        String idSqlCounter = "select count(*) from feedbacks where film_id = ? and author_id = ?;";
        Integer count = jdbcTemplate.queryForObject(idSqlCounter, Integer.class, filmId, userId);

        if (count == null || count < 1) {
            throw new NoProviderFoundException("Не найден комментарий для редактирования");
        }
        int id;

        if (count > 1 && cur == 0) {
            throw new NoProviderFoundException(
                    STR."У фильма с id=\{filmId} несколько комментов от пользователя с id=\{userId}," +
                            "добавьте в адресную строку id комментария");
        }
        if (cur != 0) {
            id = cur;
        } else {
            String getIdSql = "select id from feedbacks where film_id = ? and author_id = ?;";
            id = Objects.requireNonNull(jdbcTemplate.queryForObject(getIdSql, Integer.class, filmId, userId));
        }

        if (rate >= 0 && rate < 11) {
            String sql = "select rate from feedbacks where id = ?";
            String oldValue = String.valueOf(jdbcTemplate.queryForObject(sql, Integer.class, id));

            if (oldValue == null) {
                oldValue = "пусто";
            }

            if (!oldValue.equals(String.valueOf(rate))) {
                feedDao.addToFeed(23, userId, filmId, oldValue, String.valueOf(rate));
            }
        }

        if (!content.isEmpty()) {
            String sql = "select content from feedbacks where id = ?";
            String oldValue = jdbcTemplate.queryForObject(sql, String.class, id);

            if (oldValue == null) {
                oldValue = "пусто";
            }

            if (!oldValue.equals(content)) {
                feedDao.addToFeed(22, userId, filmId, oldValue, content);
            }
        }

        String sql = "update feedbacks set content = ?, feedback_date = ?, film_id = ?, author_id = ?, rate = ? where id = ?";

        int finalId = id;
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, content);
            preparedStatement.setTimestamp(2, feedbackDate);
            preparedStatement.setInt(3, filmId);
            preparedStatement.setInt(4, userId);
            preparedStatement.setInt(5, rate);
            preparedStatement.setInt(6, finalId);
            return preparedStatement;
        });

        if (rowsAffected > 0) {
            return Optional.of(new Feedback(id, content, rate, filmId, userId, feedbackDate));
        }
        return Optional.empty();
    }

    public String deleteComment(int id) throws ServerException {
        String idSqlCounter = "select count(*) from feedbacks where id = ?;";
        Integer count = jdbcTemplate.queryForObject(idSqlCounter, Integer.class, id);

        if (count == null || count != 1) {
            throw new NoProviderFoundException(STR."Не найден отзыв c id=\{id}");
        }
        String sql = "delete from feedbacks where id = ?;";
        int rowsAffected = jdbcTemplate.update(sql, id);

        if (rowsAffected > 0) {
            feedDao.addToFeed(24, id);
            return STR."Отзыв с id=\{id} удалён.";
        } else {
            throw new ServerException("Непредвиденная ошибка сервера.");
        }
    }
}
