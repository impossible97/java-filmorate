package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(long id, long friendId) {
        log.info("Получен PUT-запрос");
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким id " + id + " не найден!");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь с таким friendId " + friendId + " не найден!");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriend(long id, long friendId) {
        log.info("Получен DELETE-запрос");
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким id " + id + " не найден!");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь с таким friendId " + friendId + " не найден!");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);

    }

    public List<User> findAllFriends(String id) {
        log.info("Получен GET-запрос");
        int userId = Integer.parseInt(id);
        User user = userStorage.getUserById(userId);
        Set<Long> friends = user.getFriends();

        return friends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public Set<User> findCommonFriends(long id, long otherId) {
        log.info("Получен GET-запрос");

        final User userById = userStorage.getUserById(id);
        final User otherById = userStorage.getUserById(otherId);

        final Set<Long> friends = userById.getFriends();
        final Set<Long> otherFriends = otherById.getFriends();

        return friends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }

    public User getUser(String id) {
        return userStorage.getUserById(Integer.parseInt(id));
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
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        validate(user);
        return userStorage.updateUser(user);
    }
}