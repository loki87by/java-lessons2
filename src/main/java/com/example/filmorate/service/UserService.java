package com.example.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getSqlByRelationship(int stateId, boolean isFromUser) {
        String column = isFromUser ? "from_user" : "to_user";
        return STR."SELECT \{
                column} AS id FROM friendship WHERE \{
                isFromUser ? "to_user = ?" : "from_user = ?"} AND stateId = \{
                stateId}";
    }

    public boolean[] compareFriendsState(int firstId, int secondId) {
        boolean[] results = new boolean[2];
        String sql = "select count(*) from friendship where from_user = ? and to_user = ?";
        int i1 = Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Integer.class, firstId, secondId));
        results[0] = i1 > 0;
        int i2 = Objects.requireNonNull(jdbcTemplate.queryForObject(sql, Integer.class, secondId, firstId));
        results[1] = i2 > 0;
        return results;
    }
}
