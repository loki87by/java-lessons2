package com.example.catsgram.controller;

import com.example.catsgram.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.catsgram.model.Post;

import java.util.List;
import java.util.Optional;

@RestController
public class PostController {
    private static final Logger log = LoggerFactory.getLogger(PostController.class);
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public List<Post> findAll() {
        log.debug("Текущее количество постов: {}", postService.findAll().size());
        return postService.findAll();
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> findById(@PathVariable int postId) {
        Optional<Post> current = postService.findAll().stream()
                .filter(x -> x.getId() == postId).findFirst();

        if (current.isEmpty()) {
            String errorMessage = "Запрашиваемый ресурс не найден.";
            log.warn(errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
        log.debug("Нужный пост: {}", current);
        return ResponseEntity.ok(current);
    }

    @PostMapping(value = "/post")
    public Post create(@RequestBody Post post) {
        log.debug("Отправлен пост: {}", post);
        return postService.create(post);
    }
}
