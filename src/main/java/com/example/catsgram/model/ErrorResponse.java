package com.example.catsgram.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse extends Throwable {
    String error;
    String description;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }
}
