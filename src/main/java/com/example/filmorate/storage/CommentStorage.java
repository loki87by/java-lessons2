package com.example.filmorate.storage;

import com.example.filmorate.model.Feedback;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CommentStorage {
    private final JdbcTemplate jdbcTemplate;
    public CommentStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Feedback makeFeedbacks (ResultSet rs) throws SQLException {

        return new Feedback(
                rs.getInt("id"),
                rs.getString("content"),
                rs.getInt("rate"),
                rs.getInt("film_id"),
                rs.getInt("author_id"),
                rs.getTimestamp("feedback_date")
        );
    }
}
