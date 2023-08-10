package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    static UserService userService = new UserService(new InMemoryUserStorage());

    @Test
    void validateUserOk() {
        User validUser = new User();
        validUser.setName("Name");
        validUser.setLogin("login");
        validUser.setId(1);
        validUser.setEmail("user@gmail.com");
        validUser.setBirthday(LocalDate.of(1997, 5, 3));
        userService.validate(validUser);
    }

    @Test
    void validateUserFail() {
        final User user = new User();
        user.setLogin("");
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.validate(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());


        user.setLogin("userLogin");
        user.setEmail("");
        exception = assertThrows(ValidationException.class, () -> userService.validate(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());


        user.setEmail("user email");
        exception = assertThrows(ValidationException.class, () -> userService.validate(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());


        user.setEmail("user@email");
        user.setName("");
        user.setBirthday(LocalDate.MAX);
        exception = assertThrows(ValidationException.class, () -> userService.validate(user));
        assertEquals("Дата рождения пользователя не может быть в будущем", exception.getMessage());
    }
}
