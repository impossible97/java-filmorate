package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    public List<User> findFriendsByUserId(long userId) {
        log.info("Получен GET-запрос");
        String query = "SELECT friend_id FROM friendship WHERE user_id = ?";
        List<Integer> friendsId = jdbcTemplate.queryForList(query, Integer.class, userId);

        return friendsId.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public Set<User> findCommonFriends(int id, int otherId) {
        log.info("Получен GET-запрос");

        final List<User> friends = findFriendsByUserId(id);
        final List<User> otherFriends = findFriendsByUserId(otherId);
        return friends.stream()
                .filter(otherFriends::contains)
                .collect(Collectors.toSet());
    }

    public void addFriend(Integer id, Integer friendId) {
        log.info("Получен PUT-запрос");
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким id " + id + " не найден!");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь с таким friendId " + friendId + " не найден!");
        }

        String query = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";

        jdbcTemplate.update(query, id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        log.info("Получен DELETE-запрос");
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким id " + id + " не найден!");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь с таким friendId " + friendId + " не найден!");
        }

        String query = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(query, id, friendId);
    }

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
}