package com.example.catsgram.service;

import com.example.catsgram.dao.FollowDao;
import com.example.catsgram.dao.PostDao;
import com.example.catsgram.exceptions.NotFoundException;
import com.example.catsgram.model.Post;
import com.example.catsgram.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class PostService {
    private final List<Post> posts = new ArrayList<>();
    private final PostDao postDao;
    private final UserService userService;
    private final FollowDao followDao;

    public PostService (PostDao postDao, UserService userService, FollowDao followDao) {
        this.postDao=postDao;
        this.userService=userService;
        this.followDao = followDao;
    }

    public List<Post> findAll() {
        return posts;
    }
    public Collection<Post> findAllByUser(String userId) throws NotFoundException {
        User user = userService.findUserById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return postDao.findAllByUser(user);
    }
    public Collection<Post> findFollowers(String userId, int max) throws NotFoundException {
        //User user = userService.findUserById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return followDao.getFollowFeed(userId, max);
    }
    /*public List<Post> findFrom(int size, String sort, Instant from, List<Post> postsList) {
        List<Post> filtered = new ArrayList<>(postsList.stream().filter(x -> x.getCreationDate().isBefore(from)).toList());

        if (sort.equalsIgnoreCase("asc")) {
            filtered.sort(Comparator.comparing(Post::getCreationDate));
        } else {
            filtered.sort(Comparator.comparing(Post::getCreationDate).reversed());
        }
        int lastIndex = Math.min(size, filtered.size());
        return filtered.subList(0, lastIndex);
    }*/

    /*public List<Post> findAll(int size, String sort, Instant from) {
        return findFrom(size, sort, from, posts);
    }*/

    public Post create(Post post) {
        int id = (posts.size() + 1) * 13;
        post.setId(id);
        posts.add(post);
        return post;
    }
}
