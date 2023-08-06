package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {

    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage)     {
        this.userStorage = userStorage;
    }

    public String addFriend(Map<String, String> pathVarsMap) {
        int id = Integer.parseInt(pathVarsMap.get("id"));
        int friendId = Integer.parseInt(pathVarsMap.get("friendId"));
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NullPointerException("Пользователь с таким id " + id + " не найден!");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NullPointerException("Пользователь с таким friendId " + friendId + " не найден!");
        }

        if(user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        user.getFriends().add(friendId);

        if(friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }
        friend.getFriends().add(id);

        return "Пользователь " + user.getName() + " добавил в друзья пользователя " + friend.getName();
    }

    public String deleteFriend(Map<String, String> pathVarsMap) {
        int id = Integer.parseInt(pathVarsMap.get("id"));
        int friendId = Integer.parseInt(pathVarsMap.get("friendId"));
        User user = userStorage.getUserById(id);
        if (user == null) {
            return "Пользователь с таким id " + id + " не найден!";
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            return "Пользователь с таким friendId " + friendId + " не найден!";
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);

        return "Пользователь " + user.getName() + " удалил из друзей пользователя " + friend.getName();
    }

    public List<User> findAllFriends(String id) {
        int userId = Integer.parseInt(id);
        User user = userStorage.getUserById(userId);
        Set<Integer> friendsId = user.getFriends();

        List<User> friends = new ArrayList<>();
        for (Integer friendId : friendsId) {
            friends.add(userStorage.getUserById(friendId));
        }

        return friends;
    }

    public Set<User> findCommonFriends(Map<String, String> pathVarsMap) {
        int id = Integer.parseInt(pathVarsMap.get("id"));
        int otherId = Integer.parseInt(pathVarsMap.get("otherId"));

        User userById = userStorage.getUserById(id);
        User otherById = userStorage.getUserById(otherId);
        // Проверить на null

        Set<User> users = new HashSet<>();

        if (userById.getFriends() == null) {
            return users;
        }

        for(Integer userId: userById.getFriends()) {
            users.add(userStorage.getUserById(userId));
        }

        Set<User> others = new HashSet<>();
        for(Integer otherUserId: otherById.getFriends()) {
            others.add(userStorage.getUserById(otherUserId));
        }
        users.retainAll(others);

        return users;
    }
}