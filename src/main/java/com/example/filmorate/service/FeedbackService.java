package com.example.filmorate.service;

import com.example.filmorate.model.Feedback;
import com.example.filmorate.storage.CommentStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    private final JdbcTemplate jdbcTemplate;
    private final CommentStorage commentStorage;

    @Autowired
    public FeedbackService(JdbcTemplate jdbcTemplate, CommentStorage commentStorage) {
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
}
