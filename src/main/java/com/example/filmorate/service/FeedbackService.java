package com.example.filmorate.service;

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

    @Autowired
    public FeedbackService(JdbcTemplate jdbcTemplate,
                           CommentStorage commentStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.commentStorage = commentStorage;
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
            String sqlQuery = "SELECT max(id) as id from feedbacks;";
            Integer id = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
            if (id != null) {
                Feedback feedback = new Feedback(id, content, rate, filmId, userId, feedbackDate);
                return Optional.of(feedback);
            }
        }
        return Optional.empty();
    }

    public Optional<Feedback> changeComment(Feedback feedback) {
        int id = feedback.getId();
        String errorMessage;
        Timestamp feedbackDate = Timestamp.from(Instant.now());

        String checkIdSql = "select count(*) from feedbacks where id = ?";
        Integer idCounter = jdbcTemplate.queryForObject(checkIdSql, Integer.class, id);

        if (idCounter != 1) {
            errorMessage = STR."Не найден отзыв с id=\{id}";
            throw new NoProviderFoundException(errorMessage);
        }

        String checkFilmIdSql = "select count(*) from films where id = ?";
        Integer filmIdCounter = jdbcTemplate.queryForObject(checkFilmIdSql, Integer.class, feedback.getFilmId());

        if (filmIdCounter != 1 && feedback.getFilmId() != 0) {
            errorMessage = STR."Не найден фильм с id=\{feedback.getFilmId()}";
            throw new NoProviderFoundException(errorMessage);
        }

        String checkUserIdSql = "select count(*) from users where id = ?";
        Integer userIdCounter = jdbcTemplate.queryForObject(checkUserIdSql, Integer.class, feedback.getAuthor());

        if (userIdCounter != 1 && feedback.getAuthor() != 0) {
            errorMessage = STR."Не найден пользователь с id=\{feedback.getAuthor()}";
            throw new NoProviderFoundException(errorMessage);
        }

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

        int rowsAffected = FilmDBStorage.getSqlWithParams(id, feedbackParams, sqlStart, jdbcTemplate);

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
            id = jdbcTemplate.queryForObject(getIdSql, Integer.class, filmId, userId);
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

        if (count != 1) {
            throw new NoProviderFoundException(STR."Не найден отзыв c id=\{id}");
        }
        String sql = "delete from feedbacks where id = ?;";
        int rowsAffected = jdbcTemplate.update(sql, id);

        if (rowsAffected > 0) {
            return STR."Отзыв с id=\{id} удалён.";
        } else {
            throw new ServerException("Непредвиденная ошибка сервера.");
        }
    }
}
