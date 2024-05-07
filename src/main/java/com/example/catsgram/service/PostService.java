package com.example.catsgram.service;

import com.example.catsgram.model.Post;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PostService {
    private final List<Post> posts = new ArrayList<>();

    public List<Post> findAll() {
        return posts;
    }

    public List<Post> findAll(int size, String sort, Instant from) {
        List<Post> filtered = new ArrayList<>(posts.stream().filter(x -> x.getCreationDate().isBefore(from)).toList());

        if (sort.equalsIgnoreCase("asc")) {
            filtered.sort(Comparator.comparing(Post::getCreationDate));
        } else {
            filtered.sort(Comparator.comparing(Post::getCreationDate).reversed());
        }
        int lastIndex = Math.min(size, filtered.size());
        return filtered.subList(0, lastIndex);
    }

    public Post create(Post post) {
        int id = (posts.size() + 1) * 13;
        post.setId(id);
        posts.add(post);
        return post;
    }
}
