package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDbStorage {


    List<User> getAll();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Integer id);

    User getUserById(Integer id);

}
