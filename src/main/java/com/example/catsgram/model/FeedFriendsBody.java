package com.example.catsgram.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FeedFriendsBody {
    String sort = "desc";
    int size = 10;
    int page = 1;
    List<String> friends = new ArrayList<>();
}
