package com.example.filmorate.controller;

import com.example.filmorate.dao.FeedDao;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class NewsController {
    private final FeedDao feedDao;

    @Autowired
    public NewsController(FeedDao feedDao) {
        this.feedDao = feedDao;
    }
    @GetMapping("/news")
    public List<String> getHistory(
            @RequestParam (required = false, defaultValue = "5") int limit,
            @RequestParam (required = false, defaultValue = "1") int page,
            @RequestParam (required = false, defaultValue = "desc") String direction) {
        return feedDao.getHistory(limit, page, direction);
    }
}
