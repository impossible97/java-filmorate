package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    static LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    static int MIN_LENGTH = 200;

    public void addLike(long id, long userId) {
        log.info("Получен PUT-запрос");
        Film film = filmStorage.getFilmById(id);
        film.getLikes().add(userId);
    }

    public void deleteLike(long id, long userId) {
        log.info("Получен DELETE-запрос");
        Film film = filmStorage.getFilmById(id);
        userStorage.getUserById(userId);
        film.getLikes().remove(userId);
    }

    public List<Film> findFilmsByLikes(Integer count) {
        log.info("Получен GET-запрос");
        List<Film> films = filmStorage.getAll();

        return films.stream()
                .sorted(new FilmLikesComparator())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film getFilm(String id) {
        return filmStorage.getFilmById(Integer.parseInt(id));
    }

    public void validate(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > MIN_LENGTH) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Максимальная длина описания 200");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_DATE)) {
            log.error("Валидация не пройдена");
            throw new ValidationException(("Дата релиза не должна быть раньше 28 декабря 1895 года"));
        }
        if (film.getDuration() <= 0) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Продолжительность должна быть положительной");
        }
        log.info("Валидация пройдена");
    }

    public Film createFilm(Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        return filmStorage.updateFIlm(film);
    }
}
