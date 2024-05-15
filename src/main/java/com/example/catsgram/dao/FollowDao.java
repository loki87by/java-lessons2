package com.example.catsgram.dao;

import com.example.catsgram.exceptions.NotFoundException;
import com.example.catsgram.model.Post;

import java.util.List;

public interface FollowDao {
    List<Post> getFollowFeed(String userId, int max) throws NotFoundException;
    //Optional<User> create(User user);
}
