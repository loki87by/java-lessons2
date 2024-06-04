package com.example.filmorate.storage;

import com.example.filmorate.model.User;

import jakarta.validation.ValidationException;

import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserDBStorage {
    public UserDBStorage() {}

    public User makeUsers(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getString("birthday"),
                rs.getInt("id")
        );
    }

    public void checkUserBirthday(String birthday) {

        if (birthday != null) {
            String errorMessage;
            try {
                LocalDate date = LocalDate.parse(birthday);

                if (date.atStartOfDay(ZoneId.systemDefault()).toInstant().isAfter(Instant.now())) {
                    errorMessage = "Пришельцам из будущего доступ запрещен.";
                    throw new ValidationException(errorMessage);
                }
            } catch (DateTimeParseException e) {
                errorMessage = e.getMessage();
                throw new ValidationException(errorMessage);
            }
        }
    }
    public boolean isInvalidEmail(String emailValue) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(emailValue);
        return !matcher.matches();
    }
}
