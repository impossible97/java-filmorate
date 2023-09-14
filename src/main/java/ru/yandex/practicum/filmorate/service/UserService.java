package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserDbStorage userStorage;

    public User getUser(int id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public void validate(User user) {
        if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty() || (!user.getEmail().contains("@"))) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Дата рождения пользователя не может быть в будущем");
        }
    }

    public User create(User user) {
        validate(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validate(user);
        return userStorage.updateUser(user);
    }

    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }
}