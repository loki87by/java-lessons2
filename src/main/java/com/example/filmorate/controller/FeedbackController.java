package com.example.filmorate.controller;

import com.example.filmorate.model.Feedback;
import com.example.filmorate.service.FeedbackService;

import jakarta.validation.ValidationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

import java.rmi.ServerException;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class FeedbackController {
    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/comments")
    public List<Feedback> getAllComments() {
        return feedbackService.getAllComments();
    }

    @GetMapping("/films/{id}/comments")
    public List<Feedback> getComments(@PathVariable Integer id) {
        return feedbackService.getComments(id);
    }

    @PostMapping("/films/{id}/comment/{userId}")
    public Optional<Feedback> setComment(@PathVariable Integer id,
                                         @PathVariable Integer userId,
                                         @RequestParam(required = false, defaultValue = "0") int rate,
                                         @RequestParam(required = false, defaultValue = "") String content) {

        if (rate < 0 || rate > 10) {
            throw new ValidationException("Оценка может быть от 1 до 10 или 0 если без оценки.");
        }

        if (rate == 0 && content.isEmpty()) {
            throw new MissingEnvironmentVariableException("Отзыв должен содержить комментарий и/или оценку.");
        }
        return feedbackService.setComment(id, userId, content, rate);
    }

    @PutMapping("/films/{filmId}/comment/{userId}")
    public Optional<Feedback> changeComment(@PathVariable Integer filmId,
                                            @PathVariable Integer userId,
                                            @RequestParam(required = false, defaultValue = "0") int rate,
                                            @RequestParam(required = false, defaultValue = "0") int id,
                                            @RequestParam(required = false, defaultValue = "") String content) {

        if (rate < 0 || rate > 10) {
            throw new ValidationException("Оценка может быть от 1 до 10 или 0 если без оценки.");
        }

        if (rate == 0 && content.isEmpty()) {
            throw new MissingEnvironmentVariableException("Отзыв должен содержить комментарий и/или оценку.");
        }
        return feedbackService.changeComment(filmId, userId, content, rate, id);
    }

    @PutMapping("/comments")
    public Optional<Feedback> changeComment(
            @RequestBody Feedback feedback) {
        return feedbackService.changeComment(feedback);
    }

    @DeleteMapping("/comments/{id}")
    public String deleteComment(
            @PathVariable Integer id) throws ServerException {
        return feedbackService.deleteComment(id);
    }
}
