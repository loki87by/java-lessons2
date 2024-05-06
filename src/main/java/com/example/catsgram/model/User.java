package com.example.catsgram.model;

import lombok.*;

import java.time.LocalDate;

@Data
public class User {
    private String email;
    private String nickname;
    private LocalDate birthdate;
}
