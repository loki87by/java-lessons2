package com.example.catsgram.dao;

import com.example.catsgram.model.Post;
import com.example.catsgram.model.User;

import java.util.Collection;

public interface PostDao {
    Collection<Post> findAllByUser(User user);
    //Optional<User> create(User user);
}
