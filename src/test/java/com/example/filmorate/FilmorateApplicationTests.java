package com.example.filmorate;

import com.example.filmorate.model.User;
import com.example.filmorate.storage.UserDBStorage;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDBStorage userStorage;

    @Test
    public void testFindUserById() {
        System.out.println("ok");
        Optional<User> userOptional = userStorage.findById(1);
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", 1));
    }

}
