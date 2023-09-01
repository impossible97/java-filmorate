package ru.yandex.practicum.filmorate.storage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    protected int generatedId = 0;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        log.info("Получен POST-запрос");
        final int id = ++ generatedId;
        user.setId(id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.info("Получен PUT-запрос");
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с таким id " + user.getId() + " не найден!");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        log.info("Получен DELETE-запрос");
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new NotFoundException("Пользователь с таким id " + id + " не найден!");
        }
    }

    @Override
    public User getUserById(long id) {
        log.info("Получен GET-запрос");
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким id " + id + " не найден!");
        }
        return user;
    }
}
