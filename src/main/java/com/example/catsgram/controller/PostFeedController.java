package com.example.catsgram.controller;

import com.example.catsgram.model.FeedFriendsBody;
import com.example.catsgram.model.Post;
import com.example.catsgram.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class PostFeedController {
    private final PostService postService;

    @Autowired
    public PostFeedController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/feed/friends")
    public ResponseEntity<?> create(@RequestBody String string) throws Exception {
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
        for (String friend: friends) {
            List<Post> friendPosts = postService.findAll().stream()
                    .filter(x -> Objects.equals(x.getAuthor(), friend)).toList();
            posts.addAll(friendPosts);
        }
        int page = ffb.getPage();
        int size = ffb.getSize();
        String sort = ffb.getSort();
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
        /*System.out.println("\u001B[38;5;33m" + "posts: " + posts + "\u001B[0m");
        System.out.println("\u001b[32m" + "response: " + response + "\u001B[0m");*/
        return ResponseEntity.ok(response);
    }
}
