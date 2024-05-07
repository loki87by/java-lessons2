package com.example.catsgram.controller;

import com.example.catsgram.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.catsgram.model.Post;

import java.time.Instant;
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
    public List<Post> findAll(@RequestParam(required = false, defaultValue = "1") int page,
                              @RequestParam(required = false, defaultValue = "10") int size,
                              @RequestParam(required = false, defaultValue = "desc") String sort) {
        Instant now = Instant.now();
        int fullSize = page * size;
        int firstIndex = (page - 1) * size;
        List<Post> response = postService.findAll(fullSize, sort, now);

        if (response.size() < fullSize) {
            fullSize = response.size();
            firstIndex = fullSize - size;

            if (firstIndex < 0) {
                firstIndex = 0;
            }
        }
        return response.subList(firstIndex, fullSize);
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
