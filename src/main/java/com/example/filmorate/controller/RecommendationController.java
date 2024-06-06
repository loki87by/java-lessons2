package com.example.filmorate.controller;

import com.example.filmorate.dao.RecommendationDao;
import com.example.filmorate.model.Film;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.rmi.ServerException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/recommendations")
public class RecommendationController {
    private final RecommendationDao recommendationDao;

    @Autowired
    public RecommendationController(RecommendationDao recommendationDao) {
        this.recommendationDao = recommendationDao;
    }

    @GetMapping("/{userId}")
    public List<Film> getRecommendations(@PathVariable int userId) {
        return recommendationDao.getRecommendations(userId);
    }

    @PostMapping("/{userId}/{filmId}/{authorId}")
    public String recommendToFriend(@PathVariable int userId,
                                    @PathVariable int filmId,
                                    @PathVariable int authorId) throws ServerException {
        int recId = recommendationDao.recommendToFriend(userId, filmId, authorId);
        return recId < 0 ? "Такая рекомендация уже есть." : STR."Добавлена рекомендация с id=\{recId}";
    }

    @PostMapping("/{authorId}/{filmId}/All")
    public String recommendToAll(@PathVariable int authorId,
                                 @PathVariable int filmId) {
        return recommendationDao.recommendToAll(authorId, filmId);
    }

    @DeleteMapping("/{authorId}/{recId}")
    public String deleteRecommendations(@PathVariable int authorId, @PathVariable int recId) throws ServerException {
        return recommendationDao.deleteRecommendations(authorId, recId);
    }
}
