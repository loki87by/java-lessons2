package com.example.filmorate.dao;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FeedDao {
    void addToFeed(int relationId, int entity1);
    void addToFeed(int relationId, int entity1, int entity2);
    void addToFeed(int relationId, int entity1, String param1, String param2);
    void addToFeed(int relationId, int entity1, int entity2, String param1);
    void addToFeed(int relationId, int entity1, int entity2, String param1, String param2);
    void checkUpdates(String entityName, int entityId, List<String> notNullParamsList, List<Object> paramValues);
    List<String> getHistory(int limit, int page, String direction);
}