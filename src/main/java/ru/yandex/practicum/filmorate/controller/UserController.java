package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Slf4j
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable String id) {
        User user = userStorage.getUserById(Integer.parseInt(id));
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return user;
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        validate(user);
        return userStorage.create(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        return userStorage.updateUser(user);
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

    @PutMapping("/users/{id}/friends/{friendId}")
    @ResponseBody
    public String addFriend(@PathVariable Map<String, String> pathVarsMap) {
        return userService.addFriend(pathVarsMap);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    @ResponseBody
    public String deleteFriend(@PathVariable Map<String, String> pathVarsMap) {
        return userService.deleteFriend(pathVarsMap);
    }

    @GetMapping("/users/{id}/friends")
    @ResponseBody
    public List<User> findAllFriends(@PathVariable String id) {
        return userService.findAllFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    @ResponseBody
    public Set<User> findCommonFriends(@PathVariable Map<String, String> pathVarsMap) {
        return userService.findCommonFriends(pathVarsMap);
    }

}
