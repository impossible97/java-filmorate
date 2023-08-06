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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final UserStorage userStorage;
    static LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    static int MIN_LENGTH = 200;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.userStorage = userStorage;
    }

    @GetMapping("/films")
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable String id) {
        Film film = filmStorage.getFilmById(Integer.parseInt(id));
        if (film == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return film;
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    @PutMapping("/films")
    public Film updateFIlm(@Valid @RequestBody Film film) {
        validate(film);
        return filmStorage.updateFIlm(film);
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

    @PutMapping("/films/{id}/like/{userId}")
    @ResponseBody
    public String likeFilm(@PathVariable Map<String, String> pathVarsMap) {
        return filmService.likeFilm(pathVarsMap);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    @ResponseBody
    public String deleteLike(@PathVariable Map<String, String> pathVarsMap) {
        return filmService.deleteLike(pathVarsMap);
    }

    @GetMapping("/films/popular")
    @ResponseBody
    public List<Film> findFilmsByLikes(@RequestParam(defaultValue = "10") final Integer count) {
        return filmService.findFilmsByLikes(count);
    }
}
