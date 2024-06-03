package com.example.filmorate.controller;

import com.example.filmorate.model.Film;
import com.example.filmorate.service.RecommendationService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.rmi.ServerException;

import java.util.List;

@RestController
@Slf4j
public class RecommendationController {
    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/recommendations/{userId}")
    public List<Film> getRecommendations(@PathVariable int userId) {
        return recommendationService.getRecommendations(userId);
    }

    @PostMapping("/recommendations/{userId}/{filmId}/{authorId}")
    public String recommendToFriend(@PathVariable int userId,
                                    @PathVariable int filmId,
                                    @PathVariable int authorId) throws ServerException {
        int recId = recommendationService.recommendToFriend(userId, filmId, authorId);
        return recId < 0 ? "Такая рекомендация уже есть." : STR."Добавлена рекомендация с id=\{recId}";
    }

    @PostMapping("/recommendations/{authorId}/{filmId}/All")
    public String recommendToAll(@PathVariable int authorId,
                                 @PathVariable int filmId) {
        return recommendationService.recommendToAll(authorId, filmId);
    }

    @DeleteMapping("/recommendations/{authorId}/{recId}")
    public String deleteRecommendations(@PathVariable int authorId, @PathVariable int recId) throws ServerException {
        return recommendationService.deleteRecommendations(authorId, recId);
    }
}
