package com.example.catsgram.controller;

import com.example.catsgram.exceptions.*;
import com.example.catsgram.model.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice("com.example.catsgram.exceptions")
public class ErrorHandler {
    @ExceptionHandler(InvalidEmailException.class)
    public static ErrorResponse invalidEmailException(final InvalidEmailException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error: " + e.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public static ErrorResponse userAlreadyExistException(final UserAlreadyExistException e) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "error: " + e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public static ErrorResponse notFoundException(final NotFoundException e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error: " + e.getMessage());
    }

    @ExceptionHandler(IncorrectParameterException.class)
    public static ErrorResponse incorrectParameterException(final IncorrectParameterException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error: " + e.getMessage());
    }
}
