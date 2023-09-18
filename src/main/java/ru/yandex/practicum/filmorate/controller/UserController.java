package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FriendService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final FriendService friendService;
    private final FilmService filmService;

    @GetMapping("/users")
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    @ResponseBody
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        friendService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    @ResponseBody
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        friendService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    @ResponseBody
    public List<User> findAllFriends(@PathVariable int id) {
        return friendService.findFriendsByUserId(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    @ResponseBody
    public Set<User> findCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return friendService.findCommonFriends(id, otherId);
    }


    @DeleteMapping("/users/{id}")
    @ResponseBody
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }
  
    @GetMapping("/users/{id}/recommendations")
    @ResponseBody
    public List<Film> findRecommendationsFilms(@PathVariable int id) {
        return filmService.findRecommendedFilms(id);
    }
}
