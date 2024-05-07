package com.example.catsgram.service;

import com.example.catsgram.model.Post;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    private final List<Post> posts = new ArrayList<>();

    public List<Post> findAll() {
        return posts;
    }

    public Post create(Post post) {
        int id = (posts.size() + 1) * 13;
        post.setId(id);
        posts.add(post);
        return post;
    }
}
