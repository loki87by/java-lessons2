package com.example.filmorate.model;

import lombok.Data;

@Data
public class TypeIdEntity {
    int id;
    String type;

    public TypeIdEntity(int id, String type) {
        this.id = id;
        this.type = type;
    }
}
