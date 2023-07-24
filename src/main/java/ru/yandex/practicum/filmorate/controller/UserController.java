package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    private final HashMap<Integer ,User> users = new HashMap<>();
    protected int generatedId = 0;

    @GetMapping("/users")
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        log.info("Получен POST-запрос");
        validate(user);
        final int id = ++ generatedId;
        user.setId(id);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser (@RequestBody User user) {
        log.info("Получен PUT-запрос");
        validate(user);
        if (!users.containsKey(user.getId())){
            throw new ValidationException("Такого id не существует");
        }
        users.remove(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    public void validate(User user) {
        if (user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Дата рождения пользователя не может быть в будущем");
        }
    }
}
