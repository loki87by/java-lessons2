package com.example.catsgram.controller;

import com.example.catsgram.exceptions.NotFoundException;
import com.example.catsgram.model.Post;
import com.example.catsgram.service.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
public class PostFeedController {
    private final PostService postService;

    @Autowired
    public PostFeedController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/feed")
    public Map<String, Integer> feed() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "фиг тебе");
    }

    @GetMapping("/follows")
    public Collection<Post> findByUser(@RequestParam String userId,
                                       @RequestParam (required = false, defaultValue = "5") int max) throws NotFoundException {
        return postService.findFollowers(userId, max);
    }

    /*@PostMapping(value = "/feed/friends")
    public List<Post> findFriends(@RequestBody String string) throws Exception, ErrorResponse {
        ObjectMapper objectMapper = new ObjectMapper();
        FeedFriendsBody ffb;
        try {
            ffb = objectMapper.readValue(string, FeedFriendsBody.class);
        } catch (Exception e) {
            Object jsonObject = objectMapper.readValue(string, Object.class);
            ffb = objectMapper.readValue(jsonObject.toString(), FeedFriendsBody.class);
        }
        List<String> friends = ffb.getFriends();
        List<Post> posts = new ArrayList<>();
        for (String friend : friends) {
            List<Post> friendPosts = postService.findAll().stream()
                    .filter(x -> Objects.equals(x.getAuthor(), friend)).toList();
            posts.addAll(friendPosts);
        }
        int page = ffb.getPage();
        IncorrectParameterException error = null;

        if (page < 0) {
            error = new IncorrectParameterException("Ошибка с полем page");
        }
        int size = ffb.getSize();

        if (size < 0) {
            error = new IncorrectParameterException("Ошибка с полем page");
        }
        String sort = ffb.getSort();

        if (!sort.equalsIgnoreCase("desc") && !sort.equalsIgnoreCase("asc")) {
            error = new IncorrectParameterException("Ошибка с полем page");
        }

        if (error != null) {
            throw ErrorHandler.incorrectParameterException(error);
        }
        Instant now = Instant.now();
        int fullSize = page * size;
        int firstIndex = (page - 1) * size;
        List<Post> response = postService.findFrom(fullSize, sort, now, posts);

        if (response.size() < fullSize) {
            fullSize = response.size();
            firstIndex = fullSize - size;

            if (firstIndex < 0) {
                firstIndex = 0;
            }
        }
        response = response.subList(firstIndex, fullSize);
        System.out.println("\u001B[38;5;33m" + "posts: " + posts + "\u001B[0m");
        System.out.println("\u001b[32m" + "response: " + response + "\u001B[0m");
        return response;
    }*/
}
