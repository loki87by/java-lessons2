package com.example.filmorate.model;

import lombok.Data;

@Data
public class Feedback {
    int id;
    String content;
    int rate;
    int filmId;
    int author;
    String feedbackDate;

    public Feedback(int id, String content, int rate, int filmId, int author, String feedbackDate) {
        this.id = id;
        this.content = content;
        this.rate = rate;
        this.filmId = filmId;
        this.author = author;
        this.feedbackDate = feedbackDate;
    }
}
