package com.example.filmorate.controller;

import com.example.filmorate.model.ErrorResponse;
import com.example.filmorate.storage.UserDBStorage;

import jakarta.validation.NoProviderFoundException;
import jakarta.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

import java.rmi.ServerException;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ErrorHandler {
    private final UserDBStorage userDBStorage;

    @Autowired
    public ErrorHandler(UserDBStorage userDBStorage) {
        this.userDBStorage = userDBStorage;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static String invalidEmailException(final ValidationException e) {
        return new ErrorResponse("error: ", e.getMessage()).getMessage();
    }

    @ExceptionHandler(MissingEnvironmentVariableException .class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public static String hasNotDataException(final MissingEnvironmentVariableException  e) {
        return new ErrorResponse("error: ", e.getMessage()).getMessage();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static String notFoundInDBException(final NoSuchElementException e) {
        return new ErrorResponse("Вместо требуемых данных ваш запрос возвращает: ", e.getMessage()).getMessage();
    }

    @ExceptionHandler(NoProviderFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static String notFoundException(final NoProviderFoundException e) {
        return new ErrorResponse("error: ", e.getMessage()).getMessage();
    }

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public static String unsupportedServerException(final ServerException e) {
        return new ErrorResponse("error: ", e.getMessage()).getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {

                    if (error instanceof FieldError fieldError) {

                        if (fieldError.getField().equals("email")) {
                            String emailValue = (String) fieldError.getRejectedValue();

                            if (userDBStorage.isInvalidEmail(emailValue)) {
                                return "Некорректный формат email.";
                            }
                        }
                        return STR."\{fieldError.getField()}: \{fieldError.getDefaultMessage()}";
                    }
                    return STR."\{error.getObjectName()}: \{error.getDefaultMessage()}";
                })
                .toList();
        return new ErrorResponse("error: ", String.valueOf(errors)).getMessage();
    }
}