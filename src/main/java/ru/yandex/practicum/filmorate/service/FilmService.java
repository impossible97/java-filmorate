package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public String likeFilm(Map<String, String> pathVarsMap) {
        log.info("Получен PUT-запрос");
        int filmId = Integer.parseInt(pathVarsMap.get("id"));
        int userId = Integer.parseInt(pathVarsMap.get("userId"));

        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        film.setLikes(film.getLikes() + 1);

        return "Пользователь " + user.getName() + " поставил лайк фильму " + film.getName() +
                ". У фильма " + film.getName() + " " + film.getLikes() + " лайков.";
    }

    public String deleteLike(Map<String, String> pathVarsMap) {
        log.info("Получен DELETE-запрос");
        int filmId = Integer.parseInt(pathVarsMap.get("id"));
        int userId = Integer.parseInt(pathVarsMap.get("userId"));

        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        film.setLikes(film.getLikes() - 1);

        return "Пользователь " + user.getName() + " удалил лайк фильму " + film.getName() +
                ". У фильма " + film.getName() + " " + film.getLikes() + " лайков.";
    }

    public List<Film> findFilmsByLikes(Integer count) {
        log.info("Получен GET-запрос");
        List<Film> films = filmStorage.getAll();
        Collections.sort(films, new FilmLikesComparator());
        List<Film> sortedFilms = new ArrayList<>();
        if (count > films.size()) {
            count = films.size();
        }
        for (int i = 0; i < count; i++) {
            sortedFilms.add(films.get(i));
        }

        return sortedFilms;
    }
}
