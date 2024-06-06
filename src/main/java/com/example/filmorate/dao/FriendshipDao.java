package com.example.filmorate.dao;

import com.example.filmorate.model.User;

import org.springframework.stereotype.Component;

import java.rmi.ServerException;
import java.util.List;

@Component
public interface FriendshipDao {
    List<User> findUsersByFriendshipState(int userId, boolean isFromUser);

    List<User> findFriendsById(int id);

    String follow(int firstId, int secondId);

    String addFriend(int userId, int followerId);

    String removeFriend(int userId, int removedId);

    List<User> getCrossFriends(int firstId, int secondId);

    String unfollow(int id, int friendId) throws ServerException;
}
