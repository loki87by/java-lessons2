package com.example.catsgram.controller;

import com.example.catsgram.exceptions.IncorrectParameterException;
import com.example.catsgram.exceptions.NotFoundException;
import com.example.catsgram.service.PostService;
import com.example.catsgram.model.Post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                              @RequestParam(required = false, defaultValue = "desc") String sort)
            throws IncorrectParameterException {
        Instant now = Instant.now();

        if (page < 0) {
            throw new IncorrectParameterException("Ошибка с полем page");
        }

        if (size < 0) {
            throw new IncorrectParameterException("Ошибка с полем size");
        }

        if (!sort.equalsIgnoreCase("desc") && !sort.equalsIgnoreCase("asc")) {
            throw new IncorrectParameterException("Ошибка с полем sort");
        }
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
    public ResponseEntity<?> findById(@PathVariable int postId) throws NotFoundException {
        Optional<Post> current = postService.findAll().stream()
                .filter(x -> x.getId() == postId).findFirst();

        if (current.isEmpty()) {
            String errorMessage = "Пользователь с id: " + postId + " не найден.";
            log.warn(errorMessage);
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
            throw new NotFoundException(errorMessage);
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
