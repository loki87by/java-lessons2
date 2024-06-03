package com.example.filmorate.model;

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

    @Override
    public String getMessage() {
        return this.error + this.description;
    }
}
