package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    static UserController userController = new UserController();

    @Test
    void validateUserOk() {
        User validUser = new User();
        validUser.setName("Name");
        validUser.setLogin("login");
        validUser.setId(1);
        validUser.setEmail("user@gmail.com");
        validUser.setBirthday(LocalDate.of(1997, 5, 3));
        userController.validate(validUser);
    }

    @Test
    void validateUserFail() {
        final User user = new User();
        user.setLogin("");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.validate(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());


        user.setLogin("userLogin");
        user.setEmail("");
        exception = assertThrows(ValidationException.class, () -> userController.validate(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());


        user.setEmail("user email");
        exception = assertThrows(ValidationException.class, () -> userController.validate(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());


        user.setEmail("user@email");
        user.setName("");
        user.setBirthday(LocalDate.MAX);
        exception = assertThrows(ValidationException.class, () -> userController.validate(user));
        assertEquals("Дата рождения пользователя не может быть в будущем", exception.getMessage());
    }
}
