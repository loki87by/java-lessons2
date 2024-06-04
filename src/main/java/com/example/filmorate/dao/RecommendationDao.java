package com.example.filmorate.dao;

import com.example.filmorate.model.Film;
import org.springframework.stereotype.Component;

import java.rmi.ServerException;
import java.util.List;

@Component
public interface RecommendationDao {
    List<Film> getRecommendations(int userId);
    int recommendToFriend(int userId, int filmId, int authorId) throws ServerException;
    String recommendToAll(int userId, int filmId);
    String deleteRecommendations(int authorId, int recId) throws ServerException;
}
