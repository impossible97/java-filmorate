package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FriendService {

    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private final EventService eventService;

    public List<User> findFriendsByUserId(int userId) {
        log.info("Получен GET-запрос");
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким id " + userId + " не найден!");
        }
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

        final Event event = Event.builder()
            .userId(id)
            .entityId(friendId)
            .eventType(Event.EventType.FRIEND)
            .operation(Event.Operation.ADD)
            .build();
        eventService.raiseEvent(event);
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

        final Event event = Event.builder()
            .userId(id)
            .entityId(friendId)
            .eventType(Event.EventType.FRIEND)
            .operation(Event.Operation.REMOVE)
            .build();
        eventService.raiseEvent(event);
    }
}